package org.robotcontrol.middleware.idl;

public interface Watchdog {
    public void heartbeat(String serviceName);
    public void subscribe(String serviceName, String patternStr);
}
