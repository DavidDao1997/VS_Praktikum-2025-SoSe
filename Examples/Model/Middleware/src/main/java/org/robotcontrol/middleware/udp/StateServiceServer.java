package org.robotcontrol.middleware.udp;

import org.robotcontrol.middleware.RpcRequest;
import org.robotcontrol.middleware.RpcValue;
import org.robotcontrol.middleware.ServerStub;
import org.robotcontrol.middleware.ServerStub_I;

public class StateServiceServer implements ServerStub_I {
    private ServerStub stateService;

    public StateServiceServer(ServerStub stateService) {
        this.stateService = stateService;
    }
    
    public void move(RpcRequest request) {
        call(request.function(), request.values().toArray(new RpcValue[0]));
    }

    @Override
    public void call(String fnName, RpcValue... args) {
        switch (fnName) {
            case "select":
                System.out.printf("stateService.select() was called with: %s\n", args[0]);
                stateService.call(fnName, args);
                break;
        
            default:
                break;
        }
    }

}
