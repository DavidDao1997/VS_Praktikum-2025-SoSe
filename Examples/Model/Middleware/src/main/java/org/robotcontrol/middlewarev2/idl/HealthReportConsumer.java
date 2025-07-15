package org.robotcontrol.middlewarev2.idl;

public interface HealthReportConsumer {
    void reportHealth(String serviceName, boolean isAlive);
}
