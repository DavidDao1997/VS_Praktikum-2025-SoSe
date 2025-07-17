package org.robotcontrol.middleware.udp;
 
import org.robotcontrol.middleware.RpcRequest;
import org.robotcontrol.middleware.RpcValue;
import org.robotcontrol.middleware.ServerStub;
import org.robotcontrol.middleware.ServerStub_I;
 
public class ViewServer implements ServerStub_I {
    private ServerStub viewService;
 
    public ViewServer(ServerStub viewService) {
        this.viewService = viewService;
    }
    
    public void move(RpcRequest request) {
        call(request.function(), request.values().toArray(new RpcValue[0]));
    }
 
    @Override
    public void call(String fnName, RpcValue... args) {
        switch (fnName) {
            case "select":
                System.out.printf("viewService.select() was called with: %s\n", args[0]);
                viewService.call(fnName, args);
                break;
        
            default:
                break;
        }
    }
}