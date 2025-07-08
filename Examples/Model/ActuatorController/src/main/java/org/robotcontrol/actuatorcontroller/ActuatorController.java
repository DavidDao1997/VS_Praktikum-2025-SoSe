package org.robotcontrol.actuatorcontroller;
import org.cads.vs.roboticArm.hal.ICaDSRoboticArm;
import org.cads.vs.roboticArm.hal.real.CaDSRoboticArmReal;
import org.robotcontrol.actuatorcontroller.roboticarm.RoboticArmMock;
import org.robotcontrol.middleware.actuatorcontroller.ActuatorControllerServer;
import org.robotcontrol.middleware.idl.RegisterActuator;
import org.robotcontrol.middleware.registeractuator.RegisterActuatorClient;
import org.robotcontrol.middleware.rpc.RpcServer;


public class ActuatorController implements org.robotcontrol.middleware.idl.ActuatorController {
    public static void main(String[] args) {
         if (args.length != 2) {
            System.err.println(
             "Usage: ActuatorController <robotID> <actuatorID>");
            System.exit(1);
        }


        Integer robotID = Integer.parseInt(args[0]);
        Integer actuatorID = Integer.parseInt(args[1]);

        ActuatorController ac = new ActuatorController(robotID, actuatorID);

        RpcServer server = new RpcServer();
        server.addService(new ActuatorControllerServer(ac), "R"+robotID.toString()+"A"+actuatorID.toString(), "move");
        server.Listen();
        server.awaitTermination();
    }

    private int value = 50;
    private final int MIN_VALUE = 0;
    private final int MAX_VALUE = 100;
    private String actuator;
    private ICaDSRoboticArm real;

    public ActuatorController(Integer robotID, Integer actuatorID) {
        this.real = new CaDSRoboticArmReal("127.0.0.1", 50055);
        // this.real = new CaDSRoboticArmSimulation();
        // this.real = new RoboticArmMock();
        this.actuator = "A" + actuatorID.toString();
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        this.real.setBackForthPercentageTo(100);

        RegisterActuator ra = new RegisterActuatorClient();
        ra.registerActuator("R"+robotID.toString()+"A"+actuatorID.toString(), true);
    }

    @Override
    public void move(Direction actuatorDirection) {
        if (actuatorDirection == Direction.INCREASE) {
            if (value < MAX_VALUE) {
                value++;
            }
        } else if (actuatorDirection == Direction.DECREASE) {
            if (value > MIN_VALUE) {
                value--;
            }
        }
        applyValue();
    }

    private void applyValue() {
        switch (actuator) {
            case "A1":
                real.setLeftRightPercentageTo(value);
                System.out.println("Actuator A1 is set to " + value);
                break;
            case "A2":
                real.setUpDownPercentageTo(value);
                System.out.println("Actuator A2 is set to " + value);
                break;
            case "A3":
                real.setBackForthPercentageTo(value);
                System.out.println("Actuator A3 is set to " + value);
                break;
            case "A4":
                real.setOpenClosePercentageTo(value);
                System.out.println("Actuator A4 is set to " + value);
                break;
            default:
                throw new IllegalArgumentException("Invalid actuator name: " + actuator);
        }
    }

    public int getValue() {
        return value;
    }
}
