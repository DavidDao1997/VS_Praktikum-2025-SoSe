package org.robotcontrol.middlewarev2.idl;

public interface StateService extends HealthReportConsumer {
    public enum SelectDirection{
        UP,
        DOWN
    }
    public void setError(boolean err, boolean confirm);
    public void select(SelectDirection selectDirection);
}
