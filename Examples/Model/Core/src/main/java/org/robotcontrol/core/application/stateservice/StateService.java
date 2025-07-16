package org.robotcontrol.core.application.stateservice;

import lombok.Getter;
import lombok.Setter;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.robotcontrol.middleware.utils.Logger;
import org.robotcontrol.middlewarev2.Middleware;
import org.robotcontrol.middlewarev2.idl.Controller;
import org.robotcontrol.middlewarev2.idl.MoveAdapter;
import org.robotcontrol.middlewarev2.idl.Watchdog;

@Setter
public class StateService implements org.robotcontrol.middlewarev2.idl.StateService {
	private final Logger logger = new Logger("StateService");

	private MoveAdapter moveAdapter;
	private Controller controller;
	private int selectedRobot;
	private boolean error;
	private boolean confirm;
	private final Watchdog watchdog = Middleware.createWatchdogClient();
	private List<Robot> registeredRobots;
	private List<Robot> availableRobots;

	// maps to track last-report timestamps
	private final Map<String, Long> serviceTimestamps = new ConcurrentHashMap<>();
	private final Map<String, Long> subscriptionTimestamps = new ConcurrentHashMap<>();

	// Scheduler for periodic updates
	private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

	public StateService(Controller controller) {
		this.controller = controller;
		this.registeredRobots = new ArrayList<>();
		this.availableRobots = new ArrayList<>();
		this.selectedRobot = 0;
		this.error = false;
		this.confirm = false;

		// start periodic update task: sendUpdate() every 10 seconds
		scheduler.scheduleAtFixedRate(this::sendUpdate, 0, 10000, TimeUnit.MILLISECONDS);
		// schedule periodic timeout checks every second
		scheduler.scheduleAtFixedRate(this::checkTimestamps, 1, 1, TimeUnit.SECONDS);

		watchdog.subscribe("StateService", "R*");
	}

	@Override
	public void select(SelectDirection sd) {
		logger.info("select called with: %s", sd);
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
			String robotName = selectedRobot != 0 ? availableRobots.get(selectedRobot - 1).getName() : "";
			logger.info("selected: %s", robotName);
			moveAdapter.setSelected(robotName);
		}
		confirm = true;
		error = false;
		sendUpdate();
	}

	public String getSelected() {
		if (selectedRobot > 0) {
			return availableRobots.get(selectedRobot - 1).getName();
		}
		return null;
	}

	public void setError(boolean err, boolean confirm) {
		if (error != err || this.confirm != confirm) {
			error = err;
			this.confirm = confirm;
		}
	}

	@Override
	public void reportHealth(String serviceName, String subscription) {
		// record health report timestamps

		subscriptionTimestamps.put(subscription, System.currentTimeMillis());

		if (!serviceName.equals("")) {
			logger.info("registerActuator(actuatorName: %s, subscription: %s) called", serviceName, subscription);
			String robotName = serviceName.substring(0, 2);
			Robot r = new Robot(robotName);

			if (!registeredRobots.stream().map(Robot::getName).collect(Collectors.toList()).contains(robotName)) {
				registeredRobots.add(r);
			}
			int idx = registeredRobots.stream().map(Robot::getName).collect(Collectors.toList()).indexOf(robotName);
			r = registeredRobots.get(idx);

			serviceTimestamps.put(serviceName, System.currentTimeMillis());

			switch (serviceName.substring(2, 4)) {
				case "A1":
					r.setA1(true);
					break;
				case "A2":
					r.setA2(true);
					break;
				case "A3":
					r.setA3(true);
					break;
				case "A4":
					r.setA4(true);
					break;
				default:
					throw new IllegalArgumentException("Unexpected value: " + serviceName.substring(2, 4));
			}

			if (r.isAvailable() && !availableRobots.contains(r)) {
				availableRobots.add(r);
			} else if (!r.isAvailable() && availableRobots.contains(r)) {
				availableRobots.remove(r);
			}
		}
	}

	/** Stops the periodic update thread. */
	public void shutdown() {
		scheduler.shutdownNow();
	}

/**
 * Periodically checks for stale services or subscriptions and
 * renews subscriptions or marks robots unavailable if no update
 * has been received for more than 1 second.
 */
private void checkTimestamps() {
    long now = System.currentTimeMillis();
    checkServiceTimeouts(now);
    checkSubscriptionTimeouts(now);
}

/**
 * Checks for stale service health reports and handles timeouts.
 */
private void checkServiceTimeouts(long now) {
    Iterator<Map.Entry<String, Long>> iterator = serviceTimestamps.entrySet().iterator();
    while (iterator.hasNext()) {
        Map.Entry<String, Long> entry = iterator.next();
        String service = entry.getKey();
        long timestamp = entry.getValue();
        if (now - timestamp > 1000) {
            String robotName = service.substring(0, 2);
            registeredRobots.stream()
                .filter(r -> r.getName().equals(robotName))
                .findFirst()
                .ifPresent(r -> {
                    availableRobots.remove(r);
                    sendUpdate();
                });
            // resubscribe to watchdog
            renewSubscription();
            iterator.remove();
        }
    }
}

/**
 * Checks for stale subscriptions and re-subscribes if needed.
 */
private void checkSubscriptionTimeouts(long now) {
    Iterator<Map.Entry<String, Long>> iterator = subscriptionTimestamps.entrySet().iterator();
    while (iterator.hasNext()) {
        Map.Entry<String, Long> entry = iterator.next();
        String subscription = entry.getKey();
        long timestamp = entry.getValue();
        if (now - timestamp > 1000) {
            watchdog.subscribe("StateService", subscription);
            iterator.remove();
        }
    }
}

	private void sendUpdate() {
		String[] availRobots = new String[availableRobots.size() + 1];
		availRobots[0] = "";
		for (int i = 0; i < availableRobots.size(); i++) {
			availRobots[i + 1] = availableRobots.get(i).getName();
		}
		//controller.update(availRobots, selectedRobot, error, confirm);
	}

	// Method to convert String[] into a 32-byte long byte[] for Bitmap256
	private byte[] convertStringArrayToBitmap256(String[] robots) {
		StringBuilder combined = new StringBuilder();
		for (String robot : robots) {
			combined.append(robot);
		}
		byte[] bytes = combined.toString().getBytes(StandardCharsets.UTF_8);
		if (bytes.length < 32) {
			byte[] padded = new byte[32];
			System.arraycopy(bytes, 0, padded, 0, bytes.length);
			return padded;
		} else if (bytes.length > 32) {
			byte[] truncated = new byte[32];
			System.arraycopy(bytes, 0, truncated, 0, 32);
			return truncated;
		}
		return bytes;
	}

	private void renewSubscription() {
		watchdog.subscribe("StateService", "R*");
	}
}