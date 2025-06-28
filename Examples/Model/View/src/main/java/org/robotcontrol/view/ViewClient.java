package org.robotcontrol.view;

import java.util.ArrayList;
import java.util.List;

import org.robotcontrol.middleware.RpcValue;
import org.robotcontrol.middleware.udp.UdpClient;

public class ViewClient implements IView{
    UdpClient udpClient;


    public ViewClient(String host, int port) {
        udpClient = new UdpClient(host, port);
    }

   public void updateView(String[] robots, int selected, boolean error, boolean confirm) {
        List<RpcValue> Robs = new ArrayList<>();
        for(String robot: robots) {
            Robs.add(new RpcValue.StringValue(robot));
        }
        udpClient.invoke("updateView", new RpcValue.ListValue(Robs),new RpcValue.IntValue(selected), new RpcValue.BoolValue(error),new RpcValue.BoolValue(confirm));
   }

}
