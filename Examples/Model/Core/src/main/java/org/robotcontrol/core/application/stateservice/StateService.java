package org.robotcontrol.core.application.stateservice;

import lombok.Getter;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.robotcontrol.middleware.idl.Controller;
import org.robotcontrol.middleware.idl.MoveAdapter;
import org.robotcontrol.middleware.utils.Logger;

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

    // Scheduler for periodic updates
    private final ScheduledExecutorService scheduler =
        Executors.newSingleThreadScheduledExecutor();

    public StateService(Controller controller) {
        this.controller = controller;
        this.registeredRobots = new ArrayList<>();
        this.availableRobots = new ArrayList<>();
        this.selectedRobot = 0;
        this.error = false;
        this.confirm = false;

        // start periodic update task: sendUpdate() every 1 second
        scheduler.scheduleAtFixedRate(this::sendUpdate, 0, 200, TimeUnit.MILLISECONDS);
		// this.moveAdapter = moveAdapter;
    }

    @Override
    public void registerActuator(String actuatorName, boolean isAlive) {
        logger.info("registerActuator(actuatorName: %s, isAlive: %s) called", actuatorName, isAlive);
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

	// public void heartbeat(String motorName) {
	//
	// }

	public void subscribe() {

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
			System.err.println(selectedRobot);
			selectedRobot = (selectedRobot - 1 + availableRobots.size() + 1) % (availableRobots.size() + 1);
		} else if (sd == SelectDirection.DOWN) {
			selectedRobot = (selectedRobot + 1) % (availableRobots.size() + 1);
			System.err.println(selectedRobot);
		}
		if (moveAdapter != null) {
			String robotName = selectedRobot != 0 ? availableRobots.get(selectedRobot-1).getName(): "";
			logger.info("selected: %s", robotName);
			moveAdapter.setSelected(robotName);
		}
		confirm = true;
		error = false;
		// send update
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
            //sendUpdate();
        }
    }

    private void sendUpdate() {
        String[] availRobots = new String[availableRobots.size() + 1];
        availRobots[0] = "";
        for (int i = 0; i < availableRobots.size(); i++) {
            availRobots[i + 1] = availableRobots.get(i).getName();
        }
		logger.info("controller.update(%s, %s, %s, %s)", availRobots, selectedRobot, error, confirm);
        controller.update(convertStringArrayToBitmap256(availRobots), selectedRobot, error, confirm);
    }

    @Override
    public void reportHealth(String serviceName, boolean isAlive) {
        throw new UnsupportedOperationException("Unimplemented method 'reportHealth'");
    }

    /** Stops the periodic update thread. */
    public void shutdown() {
        scheduler.shutdownNow();
    }

        // Methode zur Konvertierung von String[] in ein 32 Byte langes byte[] für
    // Bitmap256
    private byte[] convertStringArrayToBitmap256(String[] robots) {
        // Berechne die Gesamtgröße des Byte-Arrays, das alle String-Bytes enthält
        StringBuilder combinedRobots = new StringBuilder();

        // Kombiniere alle Roboter-Namen zu einem einzelnen String
        for (String robot : robots) {
            combinedRobots.append(robot);
        }

        // Hole den kombinierten String und konvertiere ihn in ein byte[]
        byte[] byteArray = combinedRobots.toString().getBytes(StandardCharsets.UTF_8);

        // Wenn das Array weniger als 32 Bytes hat, fülle es mit Nullen auf
        if (byteArray.length < 32) {
            byte[] paddedArray = new byte[32];
            System.arraycopy(byteArray, 0, paddedArray, 0, byteArray.length);
            return paddedArray; // Rückgabe des gepolsterten Arrays
        }
        // Wenn das Array mehr als 32 Bytes hat, schneide es auf 32 Bytes zu
        else if (byteArray.length > 32) {
            byte[] truncatedArray = new byte[32];
            System.arraycopy(byteArray, 0, truncatedArray, 0, 32);
            return truncatedArray; // Rückgabe des gekürzten Arrays
        }

        // Wenn das Array genau 32 Bytes hat, gebe es zurück
        return byteArray;
    }
}