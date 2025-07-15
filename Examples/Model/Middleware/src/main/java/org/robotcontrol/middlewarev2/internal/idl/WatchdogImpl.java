package org.robotcontrol.middlewarev2.internal.idl;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import org.robotcontrol.middleware.utils.Logger;
import org.robotcontrol.middlewarev2.idl.HealthReportConsumer;
import org.robotcontrol.middlewarev2.idl.MoveAdapter.RobotDirection;
import org.robotcontrol.middlewarev2.idl.Watchdog;
import org.robotcontrol.middlewarev2.idl.types.RpcValue;
import org.robotcontrol.middlewarev2.internal.rpc.RpcClientImpl;
import org.robotcontrol.middlewarev2.internal.rpc.RpcServerImpl;
import org.robotcontrol.middlewarev2.rpc.Callable;
import org.robotcontrol.middlewarev2.rpc.Invokable;
import org.robotcontrol.middlewarev2.rpc.RpcServer;

public class WatchdogImpl {

    public static class Service implements Callable { 
        private static final Logger logger = new Logger("Watchdog");

        // Stores the last heartbeat timestamp for each service
        private final Map<String, Instant> lastHeartbeatTimestamps = new ConcurrentHashMap<>();
        // Scheduler for periodic timeout checks
        private final ScheduledExecutorService scheduler;
        // Timeout threshold for heartbeats (in milliseconds)
        private final Duration heartbeatTimeout = Duration.ofMillis(2000);

        // Tracks online/offline status of each observedService
        private final Map<String, Boolean> observedServices = new ConcurrentHashMap<>();

        // Maps subscriber names to regex patterns
        private final Map<String, Pattern> subscriptionPatterns = new ConcurrentHashMap<>();

        // Stores the actual subscriber instances
        private final Map<String, HealthReportConsumer> subscriberConsumers = new ConcurrentHashMap<>();

        public Service(){
            // Initialize scheduler to run timeout checks at fixed intervals
            scheduler = Executors.newSingleThreadScheduledExecutor();
            scheduler.scheduleAtFixedRate(
                this::checkTimeouts,
                heartbeatTimeout.toMillis(),
                heartbeatTimeout.toMillis(),
                TimeUnit.MILLISECONDS
            );
        }

        /**
         * Registers a subscriber to receive events for observedServices matching the given wildcard pattern (e.g., "R*").
         */
        public void subscribe(String serviceName, String patternStr) {
            if (serviceName == null || patternStr == null) {
                throw new IllegalArgumentException("serviceName, patternStr, and consumer must not be null");
            }
            // Convert wildcard to regex: '*' -> '.*'
            Pattern regex = Pattern.compile("^" + patternStr.replace("*", ".*") + "$");
            Pattern prev = subscriptionPatterns.putIfAbsent(serviceName, regex);
            // On first subscription, send current status of all matching observedServices
            // if (prev == null) {
            //     for (Map.Entry<String, Boolean> entry : observedServices.entrySet()) {
            //         String observedService = entry.getKey();
            //         boolean status = entry.getValue();
            //         if (regex.matcher(observedService).matches()) {
            //             HealthReportConsumerImpl.Client client = new HealthReportConsumerImpl.Client(serviceName);
            //             client.reportHealth(observedService,status);
                        
            //         }
            //     }
            // }
        }

        public void heartbeat(String serviceName){
            if (serviceName == null) {
                throw new IllegalArgumentException("serviceName must not be null");
            }
            // Check previous status for revival detection
            boolean wasUp = observedServices.getOrDefault(serviceName, false);
            if (!wasUp) {
                logger.info("Heartbeat for %s: now healthy", serviceName);
            }
            // System.out.println(serviceName + ": " + wasUp);
            boolean isFirstHeartbeat = !lastHeartbeatTimestamps.containsKey(serviceName);
            // System.out.println(serviceName + ": isFirstHeartbeat " + isFirstHeartbeat);
            // Record timestamp
            lastHeartbeatTimestamps.put(serviceName, Instant.now());
            // Mark observedService as up
            observedServices.put(serviceName, true);
            // Notify subscribers only on the first heartbeat
            if (isFirstHeartbeat) {
                subscriptionPatterns.entrySet().stream()
                        .filter(e -> e.getValue().matcher(serviceName).matches() && !e.getKey().equals(serviceName))
                        .map(Map.Entry::getKey)
                        .forEach(subscriber -> notifySubscriber(subscriber, serviceName, true));
            }
        }

        /**
         * Periodically invoked to detect and handle services that have not sent a heartbeat within the timeout window.
         */
        private void checkTimeouts(){
            Instant now = Instant.now();
            for (Map.Entry<String, Instant> entry : lastHeartbeatTimestamps.entrySet()) {
                String observedService = entry.getKey();
                Instant lastTime = entry.getValue();
                if (lastTime.plus(heartbeatTimeout).isBefore(now)) {
                    logger.info("Watchdog for %s: now unhealthy", observedService);
                    // Notify each subscriber whose pattern matches the timed-out observedService
                    subscriptionPatterns.entrySet().stream()
                            .filter(e -> e.getValue().matcher(observedService).matches())
                            .map(Map.Entry::getKey)
                            .forEach(subscriber -> notifySubscriber(subscriber, observedService, false));
                    // Mark observedService as down
                    observedServices.put(observedService, false);
                    // Remove timestamp to allow re-notification on next heartbeat
                    lastHeartbeatTimestamps.remove(observedService);
                }
            }
        }

        private void notifySubscriber(String subscriber, String observedService, boolean isAlive) {
            // System.out.printf("calling notifySubscriber(subscriber: %s, observedService: %s, isAlive: %s)\n",subscriber, observedService,isAlive);
            HealthReportConsumerImpl.Client client = new HealthReportConsumerImpl.Client(subscriber);
            client.reportHealth(observedService,isAlive);
        }

        @Override
        public void call(String fnName, RpcValue... args) {
           switch (fnName) {
                case "subscribe":
                    this.subscribe(
                        (String) RpcValue.unwrap(args[0]),
                        (String) RpcValue.unwrap(args[1])
                    );
                    break;
                case "setSelected":
                    this.heartbeat(
                        (String) RpcValue.unwrap(args[0])
                    );
                    break;
                default:
                    throw new IllegalArgumentException(String.format("Unknown function %s", fnName));
            }
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
                new RpcValue.StringValue(serviceName)
            );
        }

        @Override
        public void subscribe(String serviceName, String patternStr) {
            client.invoke(
                "subscribe", 
                new RpcValue.StringValue(serviceName),
                new RpcValue.StringValue(patternStr)
            );
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
