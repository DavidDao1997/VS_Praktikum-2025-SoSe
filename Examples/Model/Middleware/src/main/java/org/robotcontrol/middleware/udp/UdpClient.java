package org.robotcontrol.middleware.udp;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Arrays;

import org.robotcontrol.middleware.ClientStub_I;
import org.robotcontrol.middleware.Marshaller;
import org.robotcontrol.middleware.RpcValue;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class UdpClient implements ClientStub_I {
    private String SERVER_IP = "127.0.0.1";
    private int SERVER_PORT = 45067;



    @Override
    public void invoke(String fnName, RpcValue... args) {
        try {
            // marshal request
            String msg = Marshaller.marshal(fnName,Arrays.asList(args));

            // Create UDP socket
            DatagramSocket socket = new DatagramSocket();

            // Convert message to bytes
            byte[] sendData = msg.getBytes();

            // Server address
            InetAddress serverAddress = InetAddress.getByName(SERVER_IP);

            // Create and send packet
            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, serverAddress, SERVER_PORT);
            socket.send(sendPacket);
            System.out.println("Sent: " + msg);
            socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
}
