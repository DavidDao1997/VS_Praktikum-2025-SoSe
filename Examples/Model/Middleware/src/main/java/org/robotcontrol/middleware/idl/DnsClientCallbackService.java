package org.robotcontrol.middleware.idl;

public interface DnsClientCallbackService {
    void receiveResolution(String serviceName, String fnName, String resolvedSocket);
}