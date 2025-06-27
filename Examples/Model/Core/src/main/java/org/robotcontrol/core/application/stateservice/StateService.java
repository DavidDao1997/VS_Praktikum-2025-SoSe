package org.robotcontrol.core.application.stateservice;

import lombok.Getter;

import java.util.List;

import org.robotcontrol.middleware.ServerStub;
import org.robotcontrol.core.application.controller.rpc.Controller;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Getter
public class StateService extends ServerStub implements StateService_I{
	
	public enum SelectDirection{
		UP,
		DOWN		
	}
	private record ActuatorServerAdress(String host, int port) {
	}
	Controller controller;
	int selectedRobot;
	boolean error;
	boolean confirm;
	// names of robots that are fully available (all 4 actuators present)
	private List<String> availableRobots;
	private Map<String, Robot> robots;           // all known robots by name
    private Map<String, Long> actuatorHeartbeats;    // motorName -> last seen millis
    private static final long HEARTBEAT_TIMEOUT_MS = 5_000;   // 5 s timeout
    private ScheduledExecutorService scheduler;
	private Map<String, ActuatorServerAdress> endpoints;

	
	
	
	public StateService(Controller controller) {
		robots = new ConcurrentHashMap<>();
		availableRobots = new CopyOnWriteArrayList<>();   // holds robot names
        actuatorHeartbeats = new ConcurrentHashMap<>();
        scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(this::checkHeartbeats,
                                      HEARTBEAT_TIMEOUT_MS,
                                      HEARTBEAT_TIMEOUT_MS,
                                      TimeUnit.MILLISECONDS);
		selectedRobot = 0;
		error = false;
		confirm = false;
		this.controller = controller;
		endpoints = new ConcurrentHashMap<>();
	}
	
	public void register(String motorName, String Host, int port) {
        System.out.printf("DEBUG register(): motorName=%s, host=%s, port=%d%n", motorName, Host, port);
		// Basic validation: must be at least "R?A?"
		if (motorName == null || motorName.length() < 4) {
			throw new IllegalArgumentException("motorName invalid: " + motorName);
		}

		String robotName = motorName.substring(0, 2);      // e.g. "R1"
        System.out.printf("DEBUG register(): parsed robotName=%s%n", robotName);
		String actuator      = motorName.substring(2, 4);      // "A1"…"A4"
        System.out.printf("DEBUG register(): parsed actuator=%s%n", actuator);
		// Get or create Robot entry
		Robot r = robots.computeIfAbsent(robotName, Robot::new);
		
		//remembers which port,IP for the ActuatorServer per Actuator
		endpoints.put(motorName, new ActuatorServerAdress(Host, port));

		// Remember previous availability to decide if update is needed
		boolean wasAvailable = availableRobots.contains(robotName);

		// Mark the reported actuator as present
		switch (actuator) {
			case "A1" -> r.setA1(true);
			case "A2" -> r.setA2(true);
			case "A3" -> r.setA3(true);
			case "A4" -> r.setA4(true);
			default -> throw new IllegalArgumentException("Unexpected axis: " + actuator);
		}

        // first heartbeat right after registration
       // actuatorHeartbeats.put(motorName, System.currentTimeMillis());

		// If robot is now complete, add (once) its name to the available list
		if (r.isAvailable() && !availableRobots.contains(robotName)) {
			availableRobots.add(robotName);
			System.out.printf("DEBUG register(): Robot %s is now available",robotName );
		}

		// Send update only if availability status changed
		if (wasAvailable != availableRobots.contains(robotName)) {
			sendUpdate();
		}
	}
	
    public void heartbeat(String motorName) {
        if (motorName == null || motorName.length() < 4) return;
        actuatorHeartbeats.put(motorName, System.currentTimeMillis());
    }
	
	public void subscribe() {
		
	}

	public void select(int sd) {
		SelectDirection[] values = SelectDirection.values();
		if (sd < 0 || sd >= values.length) {
			throw new IllegalArgumentException("Invalid SelectDirection index: " + sd);
		}
		select(values[sd]);
	}
	
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
		confirm = true;
		error = false;
		// send update
		sendUpdate();
		
		
		
				
	}
	
	public String getSelected() {
		if (selectedRobot > 0) {
			return availableRobots.get(selectedRobot - 1);
		}
		return null;
	}
	
	public void setError(boolean err, boolean confirm) {
		if (error != err || this.confirm != confirm) {
			error = err;
			this.confirm = confirm;
		//  send update
			sendUpdate();			
		}
	}
	
    private void checkHeartbeats() {
        long now = System.currentTimeMillis();
        boolean availabilityChanged = false;

        for (Map.Entry<String, Long> entry : actuatorHeartbeats.entrySet()) {
            String motorName = entry.getKey();
            long lastSeen    = entry.getValue();

            if (now - lastSeen > HEARTBEAT_TIMEOUT_MS) {
                actuatorHeartbeats.remove(motorName);

                String robotName = motorName.substring(0, 2);
                String axis      = motorName.substring(2, 4);
                Robot r = robots.get(robotName);
                if (r == null) continue;

                switch (axis) {
                    case "A1" -> r.setA1(false);
                    case "A2" -> r.setA2(false);
                    case "A3" -> r.setA3(false);
                    case "A4" -> r.setA4(false);
                }

                // If robot was available but isn't anymore, remove it
                if (!r.isAvailable() && availableRobots.remove(robotName)) {
                    availabilityChanged = true;
                }
            }
        }

        if (availabilityChanged) {
            sendUpdate();
        }
    }
	
	private void sendUpdate() {
		String[] availRobots = new String[availableRobots.size()+1];
		availRobots[0] = "";
		int idx = 1;
		
		for (String name : availableRobots) {
			availRobots[idx] = name;
			idx++;
		}
		
		//update(availRobots, selectedRobot, error, confirm);
		//controller.update(availRobots,selectedRobot,error,confirm);
	}
	
}
