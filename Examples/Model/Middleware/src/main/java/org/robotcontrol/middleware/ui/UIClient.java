package org.robotcontrol.middleware.ui;

import org.robotcontrol.middleware.idl.UI;
import org.robotcontrol.middleware.rpc.RpcClient;
import org.robotcontrol.middleware.rpc.RpcValue;

public class UIClient implements UI {

    private RpcClient client;

    public UIClient() {
        client = new RpcClient("UI");
    }

    @Override
    public void updateView(byte[] robots, int selected, boolean error, boolean confirm) {
        client.invoke("updateView", new RpcValue.Bitmap256Value(robots), new RpcValue.IntValue(selected),
                new RpcValue.BoolValue(error), new RpcValue.BoolValue(confirm));

    }
}
