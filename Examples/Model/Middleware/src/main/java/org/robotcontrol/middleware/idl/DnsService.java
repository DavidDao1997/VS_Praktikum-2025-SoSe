package org.robotcontrol.middleware.idl;

public interface DnsService {
    void register(String serviceName, String functionName, String hostport);
    void resolve(String serviceName, String functionName, String clientCallbackHostport);
}
