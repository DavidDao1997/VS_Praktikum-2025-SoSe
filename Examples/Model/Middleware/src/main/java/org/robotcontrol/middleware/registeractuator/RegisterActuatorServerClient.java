package org.robotcontrol.middleware.registeractuator;

import org.robotcontrol.middleware.idl.RegisterActuator;
import org.robotcontrol.middleware.rpc.RpcClient;

public class RegisterActuatorServerClient implements RegisterActuator {

    private RpcClient client;
    
    public RegisterActuatorServerClient(){
        client = new RpcClient("registerActuator");
    }
    @Override
    public void registerActuator(String actuatorName, boolean isAlive) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'registerActuator'");
    }
    
}
