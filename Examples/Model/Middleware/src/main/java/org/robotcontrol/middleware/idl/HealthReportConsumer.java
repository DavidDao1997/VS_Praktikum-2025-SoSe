package org.robotcontrol.middleware.idl;

public interface HealthReportConsumer {
    void reportHealth(String serviceName, String subscription);
}
