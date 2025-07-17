package org.robotcontrol.actuatorcontroller;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.cads.vs.roboticArm.hal.ICaDSRoboticArm;
import org.cads.vs.roboticArm.hal.real.CaDSRoboticArmReal;
import org.robotcontrol.middleware.Middleware;
import org.robotcontrol.middleware.idl.Watchdog;
import org.robotcontrol.middleware.rpc.RpcServer;
import org.robotcontrol.middleware.utils.Environment;
import org.robotcontrol.middleware.utils.Logger;


public class ActuatorController implements org.robotcontrol.middleware.idl.ActuatorController {
    private static final Logger logger = new Logger("ActuatorController");
    public static void main(String[] args) {
         if (args.length != 2) {
            System.err.println(
             "Usage: ActuatorController <robotID> <actuatorID>");
            System.exit(1);
        }

        Integer robotID = Integer.parseInt(args[0]);
        Integer actuatorID = Integer.parseInt(args[1]);

        Integer PORT = Environment.getEnvIntOrExit("PORT");

        ActuatorController ac = new ActuatorController(robotID, actuatorID);

        RpcServer server = Middleware.createActuatorControllerServer(ac, PORT, "R"+robotID.toString()+"A"+actuatorID.toString(), "core");
        // server.listenAndServe();
        server.start();
        // server.addService(PORT, new ActuatorControllerServer(ac), "R"+robotID.toString()+"A"+actuatorID.toString(), "move");

    }

    private int value = 50;
    private final String serviceName;
    private final String actuator;
    private ICaDSRoboticArm real;
    private final Watchdog watchdog;
    private final int MIN_VALUE = 5;
    private final int MAX_VALUE = 95;
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private final int INCREMENT = 5;


    public ActuatorController(Integer robotID, Integer actuatorID) {
        this.real = new CaDSRoboticArmReal("127.0.0.1", 50055);
        // this.real = new CaDSRoboticArmSimulation();
        // this.real = new RoboticArmMock();
        this.serviceName = "R" + robotID + "A" + actuatorID;
        this.actuator = "A" + actuatorID.toString();
        // this.real.setBackForthPercentageTo(0);
        // try {
        //     Thread.sleep(INCREMENT00);
        // } catch (InterruptedException e) {
        //     // TODO Auto-generated catch block
        //     e.printStackTrace();
        // }
        // this.real.setBackForthPercentageTo(INCREMENT0);
        // RegisterActuator ra = new RegisterActuatorClient();
        // ra.registerActuator("R"+robotID.toString()+"A"+actuatorID.toString(), true);
        real.setBackForthPercentageTo(50);
        real.setLeftRightPercentageTo(50);
        real.setOpenClosePercentageTo(50);
        real.setUpDownPercentageTo(50);
        watchdog = Middleware.createWatchdogClient();
        scheduler.scheduleAtFixedRate(this::periodicHeartbeat, 500, 150, TimeUnit.MILLISECONDS);
    }

    @Override
    public void move(Direction actuatorDirection) {
        int curr;
        int next;
        switch (actuator) {
            case "A1":
                curr = real.getLeftRightPercentage();
                if (curr >= MAX_VALUE && actuatorDirection == Direction.INCREASE) return;
                if (curr <= MIN_VALUE && actuatorDirection == Direction.DECREASE) return;
                next = curr + (actuatorDirection == Direction.INCREASE ? INCREMENT : -INCREMENT);
                real.setLeftRightPercentageTo(next);
                break;
            case "A2":
                curr = real.getUpDownPercentage();
                if (curr >= MAX_VALUE && actuatorDirection == Direction.INCREASE) return;
                if (curr <= MIN_VALUE && actuatorDirection == Direction.DECREASE) return;
                next = curr + (actuatorDirection == Direction.INCREASE ? INCREMENT : -INCREMENT);
                real.setUpDownPercentageTo(next);
                break;
            case "A3":
                curr = real.getBackForthPercentage();
                if (curr >= MAX_VALUE && actuatorDirection == Direction.INCREASE) return;
                if (curr <= MIN_VALUE && actuatorDirection == Direction.DECREASE) return;
                next = curr + (actuatorDirection == Direction.INCREASE ? INCREMENT : -INCREMENT);
                real.setBackForthPercentageTo(next);
                break;
            case "A4":
                curr = real.getOpenClosePercentage();
                if (curr >= MAX_VALUE && actuatorDirection == Direction.INCREASE) return;
                if (curr <= MIN_VALUE && actuatorDirection == Direction.DECREASE) return;
                next = curr + (actuatorDirection == Direction.INCREASE ? INCREMENT : -INCREMENT);
                real.setOpenClosePercentageTo(next);
                break;
            default:
                throw new IllegalArgumentException("Invalid actuator name: " + actuator);
        }
        System.out.println("Actuator A4 is set to " + next);
    }

    public int getValue() {
        return value;
    }

    private void periodicHeartbeat() {
        watchdog.heartbeat(serviceName);
    }
}
