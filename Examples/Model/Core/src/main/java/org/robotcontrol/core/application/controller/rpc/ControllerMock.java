package org.robotcontrol.core.application.controller.rpc;

public class ControllerMock implements IController {

    @Override
    public void update(String[] robots, int selected, boolean error, boolean confirm) {
        System.out.println("update called with arguments: robots=" + String.join(", ", robots)
            + ", selected=" + selected
            + ", error=" + error
            + ", confirm=" + confirm);
    }
    
}
