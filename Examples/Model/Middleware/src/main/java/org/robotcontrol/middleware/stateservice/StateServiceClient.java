package org.robotcontrol.middleware.stateservice;

import org.robotcontrol.middleware.idl.StateService;
import org.robotcontrol.middleware.rpc.RpcClient;
import org.robotcontrol.middleware.rpc.RpcValue;

public class StateServiceClient implements StateService {
    private RpcClient client;

    public StateServiceClient() {
        client = new RpcClient("StateService");
    }

    @Override
    public void reportHealth(String serviceName) {
        client.invoke("reportHealth", new RpcValue.StringValue(serviceName));
    }
    
    @Override
    public void setError(boolean err, boolean confirm) {
        client.invoke("setError", new RpcValue.BoolValue(err), new RpcValue.BoolValue(err));
    }
}
