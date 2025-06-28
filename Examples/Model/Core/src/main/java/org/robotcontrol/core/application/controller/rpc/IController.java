package org.robotcontrol.core.application.controller.rpc;

public interface IController {

    public void update(String[] robots, int selected, boolean error, boolean confirm);

}
