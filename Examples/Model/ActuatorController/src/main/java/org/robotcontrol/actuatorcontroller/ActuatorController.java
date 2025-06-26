package org.robotcontrol.actuatorcontroller;
//import org.cads.vs.roboticArm.hal.ICaDSRoboticArm;
import org.robotcontrol.actuatorcontroller.roboticarm.RoboticArmMock;
import org.robotcontrol.middleware.ServerStub;
import org.robotcontrol.middleware.Server;
import io.grpc.BindableService;
import javafx.scene.robot.Robot;
import movementAdapter.MoveAdapterOuterClass.RobotDirection;
import org.robotcontrol.actuatorcontroller.roboticarm.RoboticArmMock;

import org.robotcontrol.middleware.services.ActuatorControllerServer;



public class ActuatorController extends ServerStub {
    public static void main(String[] args) {
        ActuatorController ac = new ActuatorController("foo", 123, "A1");
        Server server = new Server(50050,(BindableService) new ActuatorControllerServer(ac));
        server.Listen();
        server.awaitTermination();
    }

    public enum Direction {
        INCREASE,
        DECREASE
    }

    private int value = 50;
    private final int MIN_VALUE = 0;
    private final int MAX_VALUE = 100;
    private String actuator;
    //private ICaDSRoboticArm real;
    private RoboticArmMock real;

    public ActuatorController(String host, int port, String actuator) {
        // this.real = new CaDSRoboticArmReal(host, port);
        // this.real = new CaDSRoboticArmSimulation();
        this.real = new RoboticArmMock();
        this.actuator = actuator;
    }
    public void move(int md) {
		Direction[] values = Direction.values();
		if (md < 0 || md >= values.length) {
			throw new IllegalArgumentException("Invalid RobotDirection index: " + md);
		}
		move(values[md]);
	}
    public void move(Direction direction) {
        if (direction == Direction.INCREASE) {
            if (value < MAX_VALUE) {
                value++;
            }
        } else if (direction == Direction.DECREASE) {
            if (value > MIN_VALUE) {
                value--;
            }
        }
        applyValue();
    }

    private void applyValue() {
        switch (actuator) {
            case "A1":
                //real.setLeftRightPercentageTo(value);
                System.out.println("Actuator A1 is set to " + value);
                break;
            case "A2":
                //real.setUpDownPercentageTo(value);
                System.out.println("Actuator A2 is set to " + value);
                break;
            case "A3":
               // real.setBackForthPercentageTo(value);
                System.out.println("Actuator A3 is set to " + value);
                break;
            case "A4":
                //real.setOpenClosePercentageTo(value);
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
