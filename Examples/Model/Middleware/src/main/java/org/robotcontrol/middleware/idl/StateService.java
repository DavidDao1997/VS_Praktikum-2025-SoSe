package org.robotcontrol.middleware.idl;

public interface StateService extends HealthReportConsumer {
    public void setError(boolean err, boolean confirm);
}
