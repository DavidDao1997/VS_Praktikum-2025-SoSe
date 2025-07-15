package org.robotcontrol.middleware.watchdog;

import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.time.Instant;
import java.time.Duration;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.Executors;
import java.util.List;
import java.util.stream.Collectors;

import org.robotcontrol.middleware.healthreportconsumer.HealthReportConsumerClient;
import org.robotcontrol.middleware.idl.HealthReportConsumer;
import org.robotcontrol.middleware.rpc.RpcServer;
import org.robotcontrol.middleware.utils.Logger;

import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

public class WatchDog implements org.robotcontrol.middleware.idl.WatchDog{

    // Stores the last heartbeat timestamp for each service
    private final Map<String, Instant> lastHeartbeatTimestamps = new ConcurrentHashMap<>();
    // Scheduler for periodic timeout checks
    private final ScheduledExecutorService scheduler;

    // Executor for periodic subscriber notifications
    private final ScheduledExecutorService scheduled_reporthealth = Executors.newSingleThreadScheduledExecutor();

    // Timeout threshold for heartbeats (in milliseconds)
    private final Duration heartbeatTimeout = Duration.ofMillis(2000);

    // Tracks online/offline status of each observedService
    private final Map<String, Boolean> observedServices = new ConcurrentHashMap<>();

    // Maps subscriber names to regex patterns
    private final Map<String, Pattern> subscriptionPatterns = new ConcurrentHashMap<>();
    // Keeps the exact wildcard pattern (e.g. "R*") so we can send it back unaltered
    private final Map<String, String> subscriptionOriginals = new ConcurrentHashMap<>();

    // Stores the actual subscriber instances
    private final Map<String, HealthReportConsumer> subscriberConsumers = new ConcurrentHashMap<>();

    /** Cache of HealthReportConsumerClient objects – avoids re‑creating sockets each time */
    private final Map<String, HealthReportConsumerClient> clientCache = new ConcurrentHashMap<>();

    Logger logger;

    public WatchDog(){
        // Initialize scheduler to run timeout checks at fixed intervals
        scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(
            this::checkTimeouts,
            heartbeatTimeout.toMillis(),
            heartbeatTimeout.toMillis(),
            TimeUnit.MILLISECONDS
        );
        logger = new Logger("Watchdog");
        // start periodic subscriber notifications every 200 ms
        scheduled_reporthealth.scheduleAtFixedRate(this::periodicnotifySubscriber, 0, 200, TimeUnit.MILLISECONDS);
    }

    /**
     * Registers a subscriber to receive events for observedServices matching the given wildcard pattern (e.g., "R*").
     */
    public void subscribe(String subscriber, String patternStr) {
        if (subscriber == null || patternStr == null) {
            throw new IllegalArgumentException("subscriber and patternStr must not be null");
        }
        logger.info("Subscriber: %s, Service: %s", subscriber,patternStr );
        Pattern regex = Pattern.compile("^" + patternStr.replace("*", ".*") + "$");
        subscriptionPatterns.putIfAbsent(subscriber, regex);
        subscriptionOriginals.putIfAbsent(subscriber, patternStr);
    }

    public void heartbeat(String serviceName){
        if (serviceName == null) {
            throw new IllegalArgumentException("serviceName must not be null");
        }
        // Check previous status for revival detection
        boolean wasUp = observedServices.getOrDefault(serviceName, false);
        boolean isFirstHeartbeat = !lastHeartbeatTimestamps.containsKey(serviceName);
        // Record timestamp
        lastHeartbeatTimestamps.put(serviceName, Instant.now());
        // Mark observedService as up
        observedServices.put(serviceName, true);
        // Notify subscribers only on the first heartbeat
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
                // Notify each subscriber whose pattern matches the timed-out observedService
                subscriptionPatterns.entrySet().stream()
                        .filter(e -> e.getValue().matcher(observedService).matches())
                        .map(Map.Entry::getKey)
                        .forEach(subscriber -> notifySubscriber(subscriber, observedService));
                // Mark observedService as down
                observedServices.put(observedService, false);
                // Remove timestamp to allow re-notification on next heartbeat
                lastHeartbeatTimestamps.remove(observedService);
            }
        }
    }

    /**
     * Notify a single subscriber about the (current) health of a service.
     * Uses a cached HealthReportConsumerClient to avoid repeated construction.
     */
    private void notifySubscriber(String subscriber, String service) {
        // Re‑use (or lazily create) a client for this subscriber
        HealthReportConsumerClient client =
                clientCache.computeIfAbsent(subscriber, key -> new HealthReportConsumerClient(key));

        // Retrieve the originally supplied wildcard pattern
        String patternStr = subscriptionOriginals.getOrDefault(subscriber, "");

        logger.info("Report Health: pattern=%s, service=%s", patternStr, service);
        client.reportHealth(service, patternStr);
    }

    /**
     * Periodically notify each subscriber of current service status.
     */
    private void periodicnotifySubscriber() {
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

    public static void main(String[] args) {
        WatchDog wd = new WatchDog();
        RpcServer s = new RpcServer();
        s.addService(new WatchdogServer(wd), "Watchdog", "subscribe", "heartbeat");
        
        s.Listen();
        s.awaitTermination();
    }
}