package org.robotcontrol.actuatorcontroller;
//import org.cads.vs.roboticArm.hal.ICaDSRoboticArm;
import org.robotcontrol.actuatorcontroller.roboticarm.RoboticArmMock;
import org.robotcontrol.middleware.ServerStub;
import org.robotcontrol.middleware.Server;
import io.grpc.BindableService;
import javafx.scene.robot.Robot;
import movementAdapter.MoveAdapterOuterClass.RobotDirection;

import org.robotcontrol.middleware.services.ActuatorControllerServer;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import stateService.StateServiceGrpc;
import stateService.StateServiceOuterClass.RegisterRequest;
import stateService.StateServiceOuterClass.HeartbeatRequest;



public class ActuatorController extends ServerStub {
    

    public enum Direction {
        INCREASE,
        DECREASE
    }

    private int value = 50;
    private final int MIN_VALUE = 0;
    private final int MAX_VALUE = 100;
    private final String motorName;       // e.g. "R1A1"
    //private ICaDSRoboticArm real;
    private RoboticArmMock real;

    public ActuatorController(String motorName) {
        this.motorName = motorName;
        this.real = new RoboticArmMock();
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
        String actuator = motorName.substring(2, 4); // "A1", "A2", …
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


    public void shutdown() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'shutdown'");
    }
}
