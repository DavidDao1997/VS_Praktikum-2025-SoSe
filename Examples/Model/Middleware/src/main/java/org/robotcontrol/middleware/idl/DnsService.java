package org.robotcontrol.middleware.dns;

public interface DnsService {
    void register(String serviceName, String hostport);
    void resolve(String serviceName, String clientCallbackHostport);
}
