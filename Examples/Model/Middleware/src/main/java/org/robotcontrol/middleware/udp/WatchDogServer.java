package org.robotcontrol.middleware.udp;

import org.robotcontrol.middleware.RpcRequest;
import org.robotcontrol.middleware.RpcValue;
import org.robotcontrol.middleware.ServerStub;
import org.robotcontrol.middleware.ServerStub_I;

public class WatchDogServer implements ServerStub_I{
    private ServerStub watchDogService;

    public WatchDogServer (ServerStub watchDogService){
        this.watchDogService = watchDogService;
    }

    public void checkIn(RpcRequest request){
        call(request.function(), request.values().toArray(new RpcValue[0]));
    }

    public void subscribe(RpcRequest request){
         call(request.function(), request.values().toArray(new RpcValue[0]));
    }

    @Override
    public void call(String fnName, RpcValue... args) {
        switch (fnName){
            case "subscribe":
                System.out.printf("WatchDogService.subscribe() was called with: %s \n", args[0]);
                watchDogService.call(fnName, args);
                break;
            case "checkIn":
                System.out.printf("WatchDogService.checkIn() was called with: %s \n", args[0]);
                watchDogService.call(fnName, args);
                break;
            default:
                break;
        }
        
        throw new UnsupportedOperationException("Unimplemented method 'call'");
    }
    
}
