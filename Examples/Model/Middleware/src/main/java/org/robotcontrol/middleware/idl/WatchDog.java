package org.robotcontrol.middleware.idl;

public interface WatchDog {
    public void checkIn(String serviceName);
    public void subscribe(String serviceNamePattern);
}
