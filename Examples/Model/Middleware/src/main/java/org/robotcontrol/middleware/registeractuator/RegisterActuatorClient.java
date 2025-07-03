package org.robotcontrol.middleware.registeractuator;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.robotcontrol.middleware.idl.RegisterActuator;
import org.robotcontrol.middleware.watchdog.WatchdogClient;


public class RegisterActuatorClient implements RegisterActuator {
        private WatchdogClient client;
    private ScheduledExecutorService scheduler;
    private ScheduledFuture<?> heartbeatTask;

    public RegisterActuatorClient() {
        client = new WatchdogClient();
        scheduler = Executors.newSingleThreadScheduledExecutor();
    }

    @Override
    public synchronized void registerActuator(String actuatorName, boolean isAlive) {
        if (isAlive) {
            // Start sending heartbeats every 5 seconds
            if (heartbeatTask == null || heartbeatTask.isCancelled()) {
                heartbeatTask = scheduler.scheduleAtFixedRate(() -> {
                    client.heartbeat(actuatorName);
                }, 0, 250, TimeUnit.MILLISECONDS);
            }
        } else {
            // Stop the heartbeat
            if (heartbeatTask != null) {
                heartbeatTask.cancel(true);
                heartbeatTask = null;
            }
        }
    }

    // Optional: call this on shutdown to clean up resources
    public void shutdown() {
        scheduler.shutdownNow();
    }
}
