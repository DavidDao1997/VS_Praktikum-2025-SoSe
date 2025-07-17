package org.robotcontrol.middlewarev2.idl;

public interface Controller {
    // TODO make String[] less data intensive and use something like i256/8xi32/bitmap256
    void update(String[] robots, int selected, boolean error, boolean confirm);
}
