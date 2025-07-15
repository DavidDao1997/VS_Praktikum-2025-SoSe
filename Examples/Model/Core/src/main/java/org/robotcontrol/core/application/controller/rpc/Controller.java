package org.robotcontrol.core.application.controller.rpc;

import org.robotcontrol.middleware.idl.UI;

public class Controller implements org.robotcontrol.middleware.idl.Controller {

    private final UI ui;

    public Controller(UI ui) {
        this.ui = ui;
    }

    @Override
    public void update(byte[] robots, int selected, boolean error, boolean confirm) {
        // Konvertiere String[] robots in ein byte[] (anstatt Byte[] verwenden wir
        // byte[])
       // byte[] robots2Byte = convertStringArrayToBitmap256(robots);

        // UIClient client = new UIClient();
        ui.updateView(robots, selected, error, confirm);
    }


    @Override
    public void reportHealth(String serviceName, boolean isAlive) {
        throw new UnsupportedOperationException("Unimplemented method 'reportHealth'");
    }
}
