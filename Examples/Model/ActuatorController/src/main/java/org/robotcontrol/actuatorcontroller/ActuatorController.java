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
    private final int MIN_VALUE = 0;
    private final int MAX_VALUE = 100;
    private final String serviceName;
    private final String actuator;
    private ICaDSRoboticArm real;
    private final Watchdog watchdog;
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();


    public ActuatorController(Integer robotID, Integer actuatorID) {
        this.real = new CaDSRoboticArmReal("127.0.0.1", 50055);
        // this.real = new CaDSRoboticArmSimulation();
        // this.real = new RoboticArmMock();
        this.serviceName = "R" + robotID + "A" + actuatorID;
        this.actuator = "A" + actuatorID.toString();
        // this.real.setBackForthPercentageTo(0);
        // try {
        //     Thread.sleep(1000);
        // } catch (InterruptedException e) {
        //     // TODO Auto-generated catch block
        //     e.printStackTrace();
        // }
        // this.real.setBackForthPercentageTo(100);
        // RegisterActuator ra = new RegisterActuatorClient();
        // ra.registerActuator("R"+robotID.toString()+"A"+actuatorID.toString(), true);
        watchdog = Middleware.createWatchdogClient();
        scheduler.scheduleAtFixedRate(this::periodicHeartbeat, 500, 150, TimeUnit.MILLISECONDS);
    }

    @Override
    public void move(Direction actuatorDirection) {
        if (actuatorDirection == Direction.INCREASE) {
            if (value < MAX_VALUE) {
                value+=10;
            }
        } else if (actuatorDirection == Direction.DECREASE) {
            if (value > MIN_VALUE) {
                value-=10;
            }
        }
        applyValue();
    }

    private void applyValue() {
        int curr;
        switch (actuator) {
            case "A1":
                curr = real.getLeftRightPercentage();
                if (curr != value) real.setLeftRightPercentageTo(value);
                System.out.println("Actuator A1 is set to " + value);
                break;
            case "A2":
                curr = real.getUpDownPercentage();
                if (curr != value) real.setUpDownPercentageTo(value);
                System.out.println("Actuator A2 is set to " + value);
                break;
            case "A3":
                curr = real.getBackForthPercentage();
                if (curr != value) real.setBackForthPercentageTo(value);
                System.out.println("Actuator A3 is set to " + value);
                break;
            case "A4":
                curr = real.getOpenClosePercentage();
                if (curr != value) real.setOpenClosePercentageTo(value);
                System.out.println("Actuator A4 is set to " + value);
                break;
            default:
                throw new IllegalArgumentException("Invalid actuator name: " + actuator);
        }
    }

    public int getValue() {
        return value;
    }

    private void periodicHeartbeat() {
        watchdog.heartbeat(serviceName);
    }
}
