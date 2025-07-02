package org.robotcontrol.middleware.idl;

public interface WatchDog {
    public void heartbeat(String serviceName);
    public void subscribe(String serviceNamePattern, String observedService);
}
