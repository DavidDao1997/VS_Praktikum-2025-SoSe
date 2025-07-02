package org.robotcontrol.middleware.udp;

import org.robotcontrol.middleware.RpcRequest;
import org.robotcontrol.middleware.RpcValue;
import org.robotcontrol.middleware.ServerStub;
import org.robotcontrol.middleware.ServerStub_I;

@Deprecated
public class ViewServer implements ServerStub_I {
    private ServerStub_I view;


    public ViewServer(ServerStub_I view) {
        this.view = view;
    }
    
    public void updateView(RpcRequest request) {
        call(request.function(), request.values().toArray(new RpcValue[0]));
    }

    @Override
    public void call(String fnName, RpcValue... args) {
        switch (fnName) {
            case "updateView":
                System.out.printf("View: updateView() was called with: %s %s %s %s\n", args[0], args[1], args[2], args[3]);
                view.call(fnName, args);
                break;
            default:
                break;
        }
    }


}
