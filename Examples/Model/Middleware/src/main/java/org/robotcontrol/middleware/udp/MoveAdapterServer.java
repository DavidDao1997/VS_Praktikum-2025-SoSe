package org.robotcontrol.middleware.udp;

import org.robotcontrol.middleware.RpcRequest;
import org.robotcontrol.middleware.RpcValue;
import org.robotcontrol.middleware.ServerStub;
import org.robotcontrol.middleware.ServerStub_I;

public class MoveAdapterServer implements ServerStub_I {
    private ServerStub moveAdapterService;

    public MoveAdapterServer(ServerStub moveAdapterService) {
        this.moveAdapterService = moveAdapterService;
    }
    
    public void move(RpcRequest request) {
        call(request.function(), request.values().toArray(new RpcValue[0]));
    }



    @Override
    public void call(String fnName, RpcValue... args) {
        switch (fnName) {
            case "move":
                System.out.printf("MoveAdapterService.move() was called with: %s\n", args[0]);
                moveAdapterService.call(fnName, args);
                break;
        
            default:
                break;
        }
    }

}
