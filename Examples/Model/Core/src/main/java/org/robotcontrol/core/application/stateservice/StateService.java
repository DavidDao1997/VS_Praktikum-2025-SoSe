package org.robotcontrol.core.application.stateservice;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.robotcontrol.middleware.ServerStub_I;
import org.robotcontrol.middleware.rpc.RpcUtils;
import org.robotcontrol.middleware.rpc.RpcValue;
import org.robotcontrol.middleware.utils.Logger;
import org.robotcontrol.core.application.controller.rpc.IController;

@Getter
public class StateService implements org.robotcontrol.middleware.idl.StateService {
    private final Logger logger = new Logger("StateService");

    public enum SelectDirection {
        UP,
        DOWN
    }

    private final IController controller;
    private int selectedRobot;
    private boolean error;
    private boolean confirm;
    private List<Robot> registeredRobots;
    private List<Robot> availableRobots;

    // Scheduler for periodic updates
    private final ScheduledExecutorService scheduler =
        Executors.newSingleThreadScheduledExecutor();

    public StateService(IController controller) {
        this.controller = controller;
        this.registeredRobots = new ArrayList<>();
        this.availableRobots = new ArrayList<>();
        this.selectedRobot = 0;
        this.error = false;
        this.confirm = false;

        // start periodic update task: sendUpdate() every 1 second
        scheduler.scheduleAtFixedRate(this::sendUpdate, 0, 200, TimeUnit.MILLISECONDS);
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

        if (r.isAvailable() && !availableRobots.contains(r)) {
            availableRobots.add(r);
            //sendUpdate();
        } else if (!r.isAvailable() && availableRobots.contains(r)) {
            availableRobots.remove(r);
            //sendUpdate();
        }
    }

    public void subscribe() {
        // implementation if needed
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
            //sendUpdate();
            return;
        }

        if (sd == SelectDirection.UP) {
            selectedRobot = (selectedRobot - 1 + availableRobots.size() + 1) % (availableRobots.size() + 1);
        } else {
            selectedRobot = (selectedRobot + 1) % (availableRobots.size() + 1);
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
            //sendUpdate();
        }
    }

    private void sendUpdate() {
        String[] availRobots = new String[availableRobots.size() + 1];
        availRobots[0] = "";
        for (int i = 0; i < availableRobots.size(); i++) {
            availRobots[i + 1] = availableRobots.get(i).getName();
        }
        controller.update(availRobots, selectedRobot, error, confirm);
    }

    @Override
    public void reportHealth(String serviceName, boolean isAlive) {
        throw new UnsupportedOperationException("Unimplemented method 'reportHealth'");
    }

    /** Stops the periodic update thread. */
    public void shutdown() {
        scheduler.shutdownNow();
    }
}