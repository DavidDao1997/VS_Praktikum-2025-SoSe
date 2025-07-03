package org.robotcontrol.middleware.idl;

public interface StateService extends HealthReportConsumer, RegisterActuator {
    public void setError(boolean err, boolean confirm);
}
