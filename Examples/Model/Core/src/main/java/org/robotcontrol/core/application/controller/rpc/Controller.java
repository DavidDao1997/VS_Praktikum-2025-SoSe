package org.robotcontrol.core.application.controller.rpc;

import org.robotcontrol.middleware.ui.UIClient;

import org.robotcontrol.view.WebSocketView;


public class Controller implements org.robotcontrol.middleware.idl.Controller {

    private final WebSocketView view;
    private UIClient client;

    public Controller(WebSocketView view) {
        this.view = view;
        client = new UIClient();
    }

    @Override
    public void update(byte[] robots, int selected, boolean error, boolean confirm) {
        // Konvertiere String[] robots in ein byte[] (anstatt Byte[] verwenden wir
        // byte[])
       // byte[] robots2Byte = convertStringArrayToBitmap256(robots);

        client.updateView(robots, selected, error, confirm);
    }


    @Override
    public void reportHealth(String serviceName, String subscription) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'reportHealth'");
    }
}
