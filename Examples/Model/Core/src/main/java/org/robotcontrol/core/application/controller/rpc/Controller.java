package org.robotcontrol.core.application.controller.rpc;

public interface Controller {

    public void update(String[] robots, int selected, boolean error, boolean confirm);
    
}
