package org.robotcontrol.middleware.idl;

public interface ActuatorController {
    public enum Direction {
        INCREASE,
        DECREASE
    }

    public void move(Direction actuatorDirection);
}
