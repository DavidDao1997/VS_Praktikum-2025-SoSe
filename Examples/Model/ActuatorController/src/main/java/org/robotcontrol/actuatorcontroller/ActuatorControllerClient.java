package org.robotcontrol.actuatorcontroller;

import org.robotcontrol.middleware.RpcValue;
import org.robotcontrol.middleware.udp.UdpClient;

public class ActuatorControllerClient {
    UdpClient udpClient;

    public ActuatorControllerClient(String host, int port) {
        udpClient = new UdpClient(host, port);
    }

    public static void main(String[] args) {
        ActuatorControllerClient c = new ActuatorControllerClient("127.0.0.1", 45067);
        c.invoke("move", new RpcValue.IntValue(1));
    }

    void invoke(String fn, RpcValue... args) {
        udpClient.invoke(fn, args);
    }
    
}
