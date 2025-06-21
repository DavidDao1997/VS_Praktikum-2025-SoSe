package org.robotcontrol.actuatorcontroller;
import org.cads.vs.roboticArm.hal.ICaDSRoboticArm;
import org.cads.vs.roboticArm.hal.real.CaDSRoboticArmReal;

public class ActuatorController {
    public static void main(String[] args) {
        ActuatorController ac = new ActuatorController("foo", 123, "A1");
        ac.move(Direction.INCREASE);
    }

    public enum Direction {
        INCREASE,
        DECREASE
    }

    private int value = 0;
    private final int MIN_VALUE = 0;
    private final int MAX_VALUE = 100;
    private String actuator;
    private ICaDSRoboticArm real;

    public ActuatorController(String host, int port, String actuator) {
        this.real = new CaDSRoboticArmReal(host, port);
        // this.real = new CaDSRoboticArmSimulation();
        this.actuator = actuator;
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
