package org.robotcontrol.middleware.idl;

public interface StateService extends HealthReportConsumer, RegisterActuator {
    public enum SelectDirection{
        UP,
        DOWN
    }
    public void setError(boolean err, boolean confirm);
    public void select(SelectDirection selectDirection);
}
