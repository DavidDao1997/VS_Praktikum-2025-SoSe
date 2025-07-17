package org.robotcontrol.middleware.internal.idl;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import org.robotcontrol.middleware.Middleware;
import org.robotcontrol.middleware.idl.HealthReportConsumer;
import org.robotcontrol.middleware.idl.Watchdog;
import org.robotcontrol.middleware.idl.MoveAdapter.RobotDirection;
import org.robotcontrol.middleware.idl.types.RpcValue;
import org.robotcontrol.middleware.internal.rpc.RpcClientImpl;
import org.robotcontrol.middleware.internal.rpc.RpcServerImpl;
import org.robotcontrol.middleware.rpc.Callable;
import org.robotcontrol.middleware.rpc.Invokable;
import org.robotcontrol.middleware.rpc.RpcServer;
import org.robotcontrol.middleware.utils.Logger;

public class WatchdogImpl {

    public static class Service implements Callable {
        private static final Logger LOGGER = new Logger("Watchdog");

        // Stores the last heartbeat timestamp for each service
        private final Map<String, Instant> lastHeartbeatTimestamps = new ConcurrentHashMap<>();
        // Scheduler for periodic timeout checks
        private final ScheduledExecutorService timeoutChecker;

        // Executor for periodic subscriber notifications
        private final ScheduledExecutorService healthReportScheduler = Executors.newSingleThreadScheduledExecutor();

        // Timeout threshold for heartbeats (in milliseconds)
        private static final Duration HEARTBEAT_TIMEOUT = Duration.ofMillis(500);

        // Tracks online/offline status of each observedService
        private final Map<String, Boolean> observedServices = new ConcurrentHashMap<>();

        // Maps subscriber names to regex patterns
        private final Map<String, Pattern> subscriptionPatterns = new ConcurrentHashMap<>();
        // Keeps the exact wildcard pattern (e.g. "R*") so we can send it back unaltered
        private final Map<String, String> subscriptionOriginals = new ConcurrentHashMap<>();

        /**
         * Cache of HealthReportConsumerClient objects – avoids re‑creating sockets each
         * time
         */
        private final Map<String, HealthReportConsumerImpl.Client> clientCache = new ConcurrentHashMap<>();

        public Service() {
            // Initialize scheduler to run timeout checks at fixed intervals
            timeoutChecker = Executors.newSingleThreadScheduledExecutor();
            timeoutChecker.scheduleAtFixedRate(
                    this::checkTimeouts,
                    HEARTBEAT_TIMEOUT.toMillis(),
                    HEARTBEAT_TIMEOUT.toMillis(),
                    TimeUnit.MILLISECONDS);
           
            // start periodic subscriber notifications every 200 ms
            healthReportScheduler.scheduleAtFixedRate(this::notifySubscribersPeriodically, 0, 200, TimeUnit.MILLISECONDS);
        }

        /**
         * Registers a subscriber to receive events for observedServices matching the
         * given wildcard pattern (e.g., "R*").
         */
        public void subscribe(String subscriber, String patternStr) {
            if (subscriber == null || patternStr == null) {
                throw new IllegalArgumentException("subscriber and patternStr must not be null");
            }
            LOGGER.info("Subscriber: %s, Service: %s", subscriber, patternStr);
            Pattern regex = Pattern.compile("^" + patternStr.replace("*", ".*") + "$");
            subscriptionPatterns.putIfAbsent(subscriber, regex);
            subscriptionOriginals.putIfAbsent(subscriber, patternStr);
        }

        public void heartbeat(String serviceName) {
            if (serviceName == null) {
                throw new IllegalArgumentException("serviceName must not be null");
            }
            // Record timestamp
            lastHeartbeatTimestamps.put(serviceName, Instant.now());
            // Mark observedService as up
            observedServices.put(serviceName, true);
        }

        /**
         * Periodically invoked to detect and handle services that have not sent a
         * heartbeat within the timeout window.
         */
        private void checkTimeouts() {
            Instant now = Instant.now();
            for (Map.Entry<String, Instant> entry : lastHeartbeatTimestamps.entrySet()) {
                String observedService = entry.getKey();
                Instant lastTime = entry.getValue();
                if (!lastTime.plus(HEARTBEAT_TIMEOUT).isBefore(now)) {
                    // Mark observedService as down
                    observedServices.put(observedService, false);

                }
            }
        }

        /**
         * Notify a single subscriber about the (current) health of a service.
         * Uses a cached HealthReportConsumerClient to avoid repeated construction.
         */
        private void notifySubscriber(String subscriber, String service) {
            // Re‑use (or lazily create) a client for this subscriber
            HealthReportConsumerImpl.Client client = clientCache.computeIfAbsent(subscriber,
                    key -> new HealthReportConsumerImpl.Client(subscriber));

            // Retrieve the originally supplied wildcard pattern
            String patternStr = subscriptionOriginals.getOrDefault(subscriber, "");

            LOGGER.info("Report Health: pattern=%s, service=%s", patternStr, service);
            client.reportHealth(service, patternStr);
        }

        /**
         * Periodically notify each subscriber of current service status.
         */
        private void notifySubscribersPeriodically() {
            // If we have no observed services yet, still inform every subscriber
            // so they know their subscription was accepted, but no matching service
            // is available at the moment.
            if (observedServices.isEmpty()) {
                for (String subscriber : subscriptionPatterns.keySet()) {
                    notifySubscriber(subscriber, "");
                }
                return; // Nothing else to do
            }

            for (Entry<String, Pattern> subEntry : subscriptionPatterns.entrySet()) {
                String subscriber = subEntry.getKey();
                Pattern pattern = subEntry.getValue();

                for (Entry<String, Boolean> svcEntry : observedServices.entrySet()) {
                    String service = svcEntry.getKey();
                    Boolean alive = svcEntry.getValue();
                    if (pattern.matcher(service).matches()) {
                        // if service alive, send its name; otherwise send empty string
                        if (Boolean.TRUE.equals(alive)) {
                            notifySubscriber(subscriber, service);
                        } else {
                            notifySubscriber(subscriber, "");
                        }
                    }
                }
            }
        }

        @Override
        public void call(String fnName, RpcValue... args) {
            switch (fnName) {
                case "subscribe":
                    this.subscribe(
                            (String) RpcValue.unwrap(args[0]),
                            (String) RpcValue.unwrap(args[1]));
                    break;
                case "heartbeat":
                    this.heartbeat(
                            (String) RpcValue.unwrap(args[0]));
                    break;
                default:
                    throw new IllegalArgumentException(String.format("Unknown function %s", fnName));
            }
        }

        /**
         * Stop all background threads gracefully.
         */
        public void shutdown() {
            timeoutChecker.shutdown();
            healthReportScheduler.shutdown();
        }

    }

    public static class Client implements Watchdog {
        private Invokable client;

        public Client() {
            this.client = new RpcClientImpl("Watchdog", true);
        }

        @Override
        public void heartbeat(String serviceName) {
            client.invoke(
                    "heartbeat",
                    new RpcValue.StringValue(serviceName));
        }

        @Override
        public void subscribe(String serviceName, String patternStr) {
            client.invoke(
                    "subscribe",
                    new RpcValue.StringValue(serviceName),
                    new RpcValue.StringValue(patternStr));
        }
    }

    public static class Server implements RpcServer {
        private RpcServerImpl server;

        public Server(Integer port, WatchdogImpl.Service service) {
            server = new RpcServerImpl(port, service, true, "Watchdog", "subscribe", "heartbeat");
        }

        @Override
        public void listenAndServe() {
            server.listenAndServe();
        }

        @Override
        public void start() {
            server.start();
        }

        @Override
        public void stop() {
            server.stop();
        }
    }

}
