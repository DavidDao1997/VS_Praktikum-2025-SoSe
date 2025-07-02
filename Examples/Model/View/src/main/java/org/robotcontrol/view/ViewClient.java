package org.robotcontrol.view;

import java.util.ArrayList;
import java.util.List;

import org.robotcontrol.middleware.rpc.RpcClient;
import org.robotcontrol.middleware.rpc.RpcValue;

public class ViewClient implements IView{
    RpcClient client;


    public ViewClient(String host, int port) {
        client = new RpcClient("View");
    }

   public void updateView(String[] robots, int selected, boolean error, boolean confirm) {
        List<RpcValue> Robs = new ArrayList<>();
        for(String robot: robots) {
            Robs.add(new RpcValue.StringValue(robot));
        }
        client.invoke("updateView", new RpcValue.ListValue(Robs),new RpcValue.IntValue(selected), new RpcValue.BoolValue(error),new RpcValue.BoolValue(confirm));
   }

}
