package org.robotcontrol.core.application.stateservice;

import lombok.Getter;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.robotcontrol.middleware.idl.Controller;
import org.robotcontrol.middleware.idl.MoveAdapter;
import org.robotcontrol.middleware.utils.Logger;
import org.robotcontrol.middleware.watchdog.WatchdogClient;

import lombok.Setter;

@Setter
public class StateService implements org.robotcontrol.middleware.idl.StateService {
    private final Logger logger = new Logger("StateService");

    MoveAdapter moveAdapter;
    Controller controller;
    int selectedRobot;
    boolean error;
    boolean confirm;
    private List<Robot> registeredRobots;
    private List<Robot> availableRobots;

    // Subscription-level health tracking
    private Map<String, Boolean> subscriptionHealth;
    private final Map<String, Long> subscriptionLastReportTime = new ConcurrentHashMap<>();

    // Service-level health tracking
    private final Map<String, Boolean> serviceHealth = new ConcurrentHashMap<>();
    private final Map<String, Long> serviceLastReportTime = new ConcurrentHashMap<>();

    private static final long HEALTH_TIMEOUT_MS = 1000;

    private final ScheduledExecutorService scheduler =
        Executors.newSingleThreadScheduledExecutor();
    private final ScheduledExecutorService periodicSubscriptionHealthCheck =
        Executors.newSingleThreadScheduledExecutor();
    // Executor for service-level health checks
    private final ScheduledExecutorService periodicServiceHealthCheckExecutor =
        Executors.newSingleThreadScheduledExecutor();

    WatchdogClient watchdogClient;

    public StateService(Controller controller) {
        this.subscriptionHealth = new ConcurrentHashMap<>();
        this.controller = controller;
        this.registeredRobots = new ArrayList<>();
        this.availableRobots = new ArrayList<>();
        this.selectedRobot = 0;
        this.error = false;
        this.confirm = false;

        // start periodic update task: sendUpdate() every 200 ms
        scheduler.scheduleAtFixedRate(this::sendUpdate, 0, 200, TimeUnit.MILLISECONDS);
        // subscription-level health check every 200 ms
        periodicSubscriptionHealthCheck.scheduleAtFixedRate(this::periodicSubscriptionHealthCheck, 0, 200, TimeUnit.MILLISECONDS);
        // service-level health check every 200 ms
        periodicServiceHealthCheckExecutor.scheduleAtFixedRate(this::periodicServiceHealthCheck, 0, 200, TimeUnit.MILLISECONDS);

        watchdogClient = new WatchdogClient();
        watchdogClient.subscribe("ServiceState", "R*");
        subscriptionHealth.putIfAbsent("R*", false);
        subscriptionLastReportTime.put("R*",System.currentTimeMillis());
    }

    @Override
    public void registerActuator(String actuatorName, boolean isAlive) {
        logger.info("registerActuator(actuatorName: {}, isAlive: {}) called", actuatorName, isAlive);
        String robotName = actuatorName.substring(0, 2);
        Robot r = new Robot(robotName);
		
		if (!registeredRobots.stream().map(Robot::getName).collect(Collectors.toList()).contains(robotName)) {
			registeredRobots.add(r);
		}
		int idx = registeredRobots.stream().map(Robot::getName).collect(Collectors.toList()).indexOf(robotName);
		logger.warn("selectedRobot: %s idx: %s isAlive: %s", selectedRobot, idx, isAlive);
		if (selectedRobot == idx + 1 && isAlive == false) {
			selectedRobot = 0;
			moveAdapter.setSelected("");
			sendUpdate();
		}
		r = registeredRobots.get(idx);
		
		switch (actuatorName.substring(2, 4)) {
			case "A1": 
				r.setA1(isAlive);
				break;
			case "A2":
				r.setA2(isAlive);
				break;
			case "A3":
				r.setA3(isAlive);
				break;
			case "A4":
				r.setA4(isAlive);
				break;

			default:
				throw new IllegalArgumentException("Unexpected value: " + actuatorName.substring(2, 4));
		}
		// check if availabeRobots can be updated
		if (r.isAvailable() && !availableRobots.contains(r)) {
			availableRobots.add(r);

			// send update do not update error, selectedRobot
			sendUpdate();
		} else if (!r.isAvailable() && availableRobots.contains(r)) {
			availableRobots.remove(r);
			sendUpdate();
		}
	}


    public void select(int sd) {
        SelectDirection[] values = SelectDirection.values();
        if (sd < 0 || sd >= values.length) {
            throw new IllegalArgumentException("Invalid SelectDirection index: " + sd);
        }
        select(values[sd]);
    }

    @Override
    public void select(SelectDirection sd) {
        if (availableRobots.isEmpty()) {
            error = true;
            confirm = false;
            sendUpdate();
            return;
        }

        if (sd == SelectDirection.UP) {
            selectedRobot = (selectedRobot - 1 + availableRobots.size() + 1) % (availableRobots.size() + 1);
        } else if (sd == SelectDirection.DOWN) {
            selectedRobot = (selectedRobot + 1) % (availableRobots.size() + 1);
        }

        if (moveAdapter != null) {
            String robotName = (selectedRobot != 0) ? availableRobots.get(selectedRobot - 1).getName() : "";
            logger.info("selected: %s", robotName);
            moveAdapter.setSelected(robotName);
        }
        confirm = true;
        error = false;
    }

    public String getSelected() {
        return (selectedRobot > 0) ? availableRobots.get(selectedRobot - 1).getName() : null;
    }

    public void setError(boolean err, boolean confirm) {
        if (error != err || this.confirm != confirm) {
            error = err;
            this.confirm = confirm;
        }
    }

    private void sendUpdate() {
        String[] availNames = new String[availableRobots.size() + 1];
        availNames[0] = "";
        for (int i = 0; i < availableRobots.size(); i++) {
            availNames[i + 1] = availableRobots.get(i).getName();
        }
		logger.info("controller.update(%s, %s, %s, %s)", availNames, selectedRobot, error, confirm);
        controller.update(convertStringArrayToBitmap256(availNames), selectedRobot, error, confirm);
    }

@Override
public void reportHealth(String serviceName, String subscription) {
    logger.info("Received health report for service %s subscription %s", serviceName, subscription);

    long now = System.currentTimeMillis();

    // ---- Subscription-level update ----
    subscriptionHealth.put(subscription, true);
    subscriptionLastReportTime.put(subscription, now);

    // ---- Service-level update ----
    // ConcurrentHashMap erlaubt keine null-Werte – also einfach (über)schreiben.
    // Bei einem neuen Dienst wird der Eintrag angelegt, sonst nur aktualisiert.
    serviceHealth.put(serviceName, true);
    serviceLastReportTime.put(serviceName, now);
}

  
    private void periodicSubscriptionHealthCheck() {
        long now = System.currentTimeMillis();
       
        for (Entry<String, Long> entry : subscriptionLastReportTime.entrySet()) {
            String subscription = entry.getKey();
            long last = entry.getValue();
            if (now - last > HEALTH_TIMEOUT_MS) {
                logger.info("Subscription timeout for %s. Re-subscribing and marking not alive.", subscription);
                watchdogClient.subscribe("ServiceState", subscription);
                subscriptionHealth.put(subscription, false);
                subscriptionLastReportTime.put(subscription, now);
            }
        }
    }

    private void periodicServiceHealthCheck() {
        long now = System.currentTimeMillis();
        for (Entry<String, Long> entry : serviceLastReportTime.entrySet()) {
            String service = entry.getKey();
            long last = entry.getValue();
            if (now - last > HEALTH_TIMEOUT_MS) {
                logger.info("Health timeout for service {}. Marking not alive.", service);
                serviceHealth.put(service, false);
                serviceLastReportTime.put(service, now);
                // mark corresponding actuator as not alive
                registerActuator(service, false);
            }
        }
    }

    /** Stops all periodic tasks. */
    public void shutdown() {
        scheduler.shutdownNow();
        periodicSubscriptionHealthCheck.shutdownNow();
        periodicServiceHealthCheckExecutor.shutdownNow();
    }

    // Converts a String[] into a 32-byte array for Bitmap256 display.
    private byte[] convertStringArrayToBitmap256(String[] robots) {
        StringBuilder combinedRobots = new StringBuilder();
        for (String robot : robots) {
            combinedRobots.append(robot);
        }
        byte[] byteArray = combinedRobots.toString().getBytes(StandardCharsets.UTF_8);
        if (byteArray.length < 32) {
            byte[] paddedArray = new byte[32];
            System.arraycopy(byteArray, 0, paddedArray, 0, byteArray.length);
            return paddedArray;
        } else if (byteArray.length > 32) {
            byte[] truncatedArray = new byte[32];
            System.arraycopy(byteArray, 0, truncatedArray, 0, 32);
            return truncatedArray;
        }
        return byteArray;
    }
}