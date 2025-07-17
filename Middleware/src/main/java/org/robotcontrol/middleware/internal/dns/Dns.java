package org.robotcontrol.middleware.internal.dns;

public interface Dns {
    void register(String serviceName, String functionName, String socket);
    String resolve(String serviceName, String functionName);
}
