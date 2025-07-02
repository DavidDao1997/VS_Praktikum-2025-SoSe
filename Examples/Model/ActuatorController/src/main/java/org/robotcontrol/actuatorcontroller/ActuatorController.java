package org.robotcontrol.actuatorcontroller;
import org.cads.vs.roboticArm.hal.ICaDSRoboticArm;
import org.cads.vs.roboticArm.hal.real.CaDSRoboticArmReal;
import org.robotcontrol.actuatorcontroller.roboticarm.RoboticArmMock;
import org.robotcontrol.middleware.actuatorcontroller.ActuatorControllerServer;
import org.robotcontrol.middleware.rpc.RpcServer;


public class ActuatorController implements org.robotcontrol.middleware.idl.ActuatorController {
    public static void main(String[] args) {
        ActuatorController ac = new ActuatorController("localhost", 50055 , "A1");

        RpcServer server = new RpcServer();
        server.addService(new ActuatorControllerServer(ac), "R1A1", "move");
        server.Listen();
        server.awaitTermination();
    }

    private int value = 50;
    private final int MIN_VALUE = 0;
    private final int MAX_VALUE = 100;
    private String actuator;
    private ICaDSRoboticArm real;

    public ActuatorController(String host, int port, String actuator) {
        // this.real = new CaDSRoboticArmReal(host, port);
        // this.real = new CaDSRoboticArmSimulation();
        this.real = new RoboticArmMock();
        this.actuator = actuator;
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
