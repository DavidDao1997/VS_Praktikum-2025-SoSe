package org.robotcontrol.middlewarev2.idl;

public interface ActuatorController {
    public enum Direction {
        INCREASE,
        DECREASE
    }

    public void move(Direction actuatorDirection);
}
