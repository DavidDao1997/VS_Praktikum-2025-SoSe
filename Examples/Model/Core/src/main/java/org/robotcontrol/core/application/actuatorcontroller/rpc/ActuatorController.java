package org.robotcontrol.core.application.actuatorcontroller.rpc;

public interface ActuatorController {
    public enum ActuatorDirection{
        INCREASE,
        DECREASE
    };
    public void move(ActuatorDirection ad);
}
