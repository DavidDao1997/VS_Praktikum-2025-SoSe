package org.robotcontrol.middleware.dns;

public interface DnsService {
    void register(String serviceName, String functionName, String hostport);
    void resolve(String serviceName, String functionName, String clientCallbackHostport);
}
