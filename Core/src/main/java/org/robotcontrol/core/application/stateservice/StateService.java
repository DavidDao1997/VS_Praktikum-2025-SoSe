package org.robotcontrol.core.application.stateservice;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.robotcontrol.middleware.Middleware;
import org.robotcontrol.middleware.idl.Controller;
import org.robotcontrol.middleware.idl.MoveAdapter;
import org.robotcontrol.middleware.idl.Watchdog;
import org.robotcontrol.middleware.utils.Logger;

import lombok.Setter;

@Setter
public class StateService implements org.robotcontrol.middleware.idl.StateService {
	private final Logger logger = new Logger("StateService");

	private MoveAdapter moveAdapter;
	private Controller controller;
	private int selectedRobot;
	private boolean error;
	private boolean confirm;
	private final Watchdog watchdog = Middleware.createWatchdogClient();
	private final CopyOnWriteArrayList<Robot> registeredRobots; 
	private final CopyOnWriteArrayList<Robot> availableRobots;

	// maps to track last-report timestamps
	private final Map<String, Long> serviceTimestamps = new ConcurrentHashMap<>();
	private final Map<String, Long> subscriptionTimestamps = new ConcurrentHashMap<>();

	// Scheduler for periodic updates
	private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

	public StateService(Controller controller) {
		this.controller = controller;
		this.registeredRobots = new CopyOnWriteArrayList<>();
		this.availableRobots = new CopyOnWriteArrayList<>();
		this.selectedRobot = 0;
		this.error = false;
		this.confirm = false;


		// start periodic update task: sendUpdate() every 10 seconds
		scheduler.scheduleAtFixedRate(this::sendUpdate, 0, 250, TimeUnit.MILLISECONDS);
		// schedule periodic timeout checks every second
		scheduler.scheduleAtFixedRate(this::checkTimestamps, 1, 200, TimeUnit.MILLISECONDS);

		scheduler.scheduleAtFixedRate(() -> watchdog.subscribe("StateService", "R*"), 1, 5, TimeUnit.SECONDS);
	}
	
	public void setMoveAdapter(MoveAdapter moveAdapter) {
		this.moveAdapter = moveAdapter;
	}

	@Override
	public void select(SelectDirection sd) {
		logger.info("select called with: %s", sd);
		if (availableRobots.isEmpty()) {
			error = true;
			confirm = false;
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
		//sendUpdate();
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
			logger.trace("registerActuator(actuatorName: %s, subscription: %s) called", serviceName, subscription);
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
						removeAvailableRobot(r);
					});
				
				iterator.remove();
			}
		}
	}

	private void removeAvailableRobot(Robot robot) {
		System.out.println("Trying to remove");
		int idx = availableRobots.indexOf(robot) + 1;
		logger.error("attempting to remove robot %s (idx: %s), currently selected %s", robot.getName(), idx, selectedRobot);
		if(idx <= 0) return;
		if (idx == selectedRobot) {
			selectedRobot = 0;
			if (moveAdapter != null) moveAdapter.setSelected("");
		}
		if(idx < selectedRobot) selectedRobot--;
		availableRobots.remove(robot);
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
		controller.update(availRobots, selectedRobot, error, confirm);
		System.out.printf("Controller.update debug - availRobots=%s, selectedRobot=%d, error=%b, confirm=%b\n", Arrays.toString(availRobots), selectedRobot, error, confirm);

	}



	private void renewSubscription() {
		watchdog.subscribe("StateService", "R*");
	}


}