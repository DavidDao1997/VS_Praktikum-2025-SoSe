package org.robotcontrol.middleware.udp;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import org.robotcontrol.middleware.ClientStub_I;
import org.robotcontrol.middleware.Marshaller;
import org.robotcontrol.middleware.RpcValue;
import org.robotcontrol.middleware.dns.Dns;
import org.robotcontrol.middleware.dns.DnsClient;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class UdpClient implements ClientStub_I {
    private String serviceName;
    private Dns dns = new DnsClient();

    public UdpClient(String host, int port) {
        throw new UnsupportedOperationException("WIP: This method is a work in progress.");
    }

    public UdpClient(String serviceName) {
        this.serviceName = serviceName;
    }

    @Override
    public void invoke(String fnName, RpcValue... args) {
        try {
            // marshal request
            String msg = Marshaller.marshal(fnName,args);

            // Create UDP socket
            DatagramSocket socket = new DatagramSocket();

            // Convert message to bytes
            byte[] sendData = msg.getBytes();

            String resolvedSocket = dns.resolve(serviceName, fnName);
            String[] socketParts = resolvedSocket.split(":", 2);
            String host = socketParts[0];
            int port = Integer.parseInt(socketParts[1]);
            InetAddress serverAddress = InetAddress.getByName(host);

            // Create and send packet
            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, serverAddress, port);
            socket.send(sendPacket);
            System.out.println("Sent: " + msg);
            socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
}
