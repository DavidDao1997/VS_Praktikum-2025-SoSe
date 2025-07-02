package org.robotcontrol.middleware.actuatorcontroller;

import org.robotcontrol.middleware.idl.ActuatorController;
import org.robotcontrol.middleware.rpc.RpcClient;
import org.robotcontrol.middleware.rpc.RpcValue;

public class ActuatorControllerClient implements ActuatorController {
    private RpcClient client;
    
    public ActuatorControllerClient() {
        client = new RpcClient("ActuatorController");
    }

    @Override
    public void move(Direction actuatorDirection) {
        client.invoke("move", new RpcValue.IntValue(actuatorDirection.ordinal()));
    }
}
