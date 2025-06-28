package org.robotcontrol.middleware.udp;

import org.robotcontrol.middleware.RpcValue;
import org.robotcontrol.middleware.ServerStub;
import org.robotcontrol.middleware.ServerStub_I;

public class ActuatorControllerServer implements ServerStub_I {
    private ServerStub actuatorController;
    public ActuatorControllerServer(ServerStub actuatorController) {
        this.actuatorController = actuatorController;
    }

    @Override
    public void call(String fnName, RpcValue... args) {
        switch (fnName) {
            case "move":
                actuatorController.call(fnName, args);
                break;
            default:
                break;
        }
    }
    
}
