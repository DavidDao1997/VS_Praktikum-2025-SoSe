package org.robotcontrol.middleware.rpc;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;

import org.robotcontrol.middleware.ClientStub_I;
import org.robotcontrol.middleware.dns.Dns;
import org.robotcontrol.middleware.dns.DnsClient;

public class RpcClient implements ClientStub_I {
    private String serviceName;
    private InetSocketAddress socketAddr;
    private Dns dns;

    public RpcClient(String serviceName) {
        this.serviceName = serviceName;
        dns = new DnsClient();
    }

    public RpcClient(InetSocketAddress socketAddr) {
        this.socketAddr = socketAddr;
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

            if (serviceName != null) {
                String resolvedSocket = dns.resolve(serviceName, fnName);
                String[] socketParts = resolvedSocket.split(":", 2);
                String host = socketParts[0];
                int port = Integer.parseInt(socketParts[1]);
                socketAddr = InetSocketAddress.createUnresolved(host, port);
            }

            // Create and send packet
            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, socketAddr);
            socket.send(sendPacket);
            socket.close();
        } catch (Exception e) {
            e.printStackTrace();
            // TODO Handle Exception gracefully
            System.exit(1);
        }
	}
}
