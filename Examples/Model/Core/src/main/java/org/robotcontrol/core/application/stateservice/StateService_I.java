package org.robotcontrol.core.application.stateservice;

public interface StateService_I {
    public void setError(boolean err, boolean confirm);
    public String getSelected();
}
