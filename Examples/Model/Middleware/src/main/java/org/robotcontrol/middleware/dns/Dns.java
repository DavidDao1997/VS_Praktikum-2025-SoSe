package org.robotcontrol.middleware.dns;

public interface Dns {
    public void register(String serviceName, String functionName, String socket);
    public String resolve(String serviceName, String functionName);
}
