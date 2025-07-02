package org.robotcontrol.middleware.idl;

public interface Controller extends HealthReportConsumer {
    // TODO make String[] less data intensive and use something like i256/8xi32/bitmap256
    void updateView(String[] robots, int selected, boolean error, boolean confirm);
}