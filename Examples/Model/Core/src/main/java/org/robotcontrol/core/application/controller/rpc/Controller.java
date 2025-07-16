package org.robotcontrol.core.application.controller.rpc;

import org.robotcontrol.middlewarev2.Middleware;
import org.robotcontrol.middlewarev2.idl.UI;

public class Controller implements org.robotcontrol.middlewarev2.idl.Controller {

    private final UI ui;

    public Controller() {
        this.ui = Middleware.createUIClient();
    }

    @Override
    public void update(String[] robots, int selected, boolean error, boolean confirm) {
        ui.updateView(robots, selected, error, confirm);
    }

    @Override
    public void reportHealth(String serviceName, String subscription) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'reportHealth'");
    }
}
