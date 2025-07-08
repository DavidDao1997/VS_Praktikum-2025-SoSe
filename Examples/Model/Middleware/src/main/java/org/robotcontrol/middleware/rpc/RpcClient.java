package org.robotcontrol.middleware.rpc;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;

import org.robotcontrol.middleware.ClientStub_I;
import org.robotcontrol.middleware.dns.Dns;
import org.robotcontrol.middleware.dns.DnsClient;
import org.robotcontrol.middleware.utils.Logger;

public class RpcClient implements ClientStub_I {
    private final Logger logger = new Logger("RpcClient");
    private String serviceName;
    private String socketAddr;
    private Dns dns;

    public RpcClient(String serviceName) {
        this.serviceName = serviceName;
        dns = new DnsClient();
    }

    public RpcClient(String host, int port) {
        this.socketAddr = host + ":" + Integer.toString(port);
    }

	@Override
	public void invoke(String fnName, RpcValue... args) {
        logger.debug("invoking: %s", fnName);
		try {
            // marshal request
            String msg = Marshaller.marshal(fnName,args);

            

            // Convert message to bytes
            byte[] sendData = msg.getBytes();

            if (serviceName != null) {
                String resolvedSocket = dns.resolve(serviceName, fnName);
                if(resolvedSocket == ""){
                    return;
                }
                logger.debug("DNS resloved: %s", resolvedSocket);
                socketAddr = resolvedSocket;
                if (resolvedSocket != "") {
                    logger.debug("DNS resloved: %s", resolvedSocket);
                } else {
                    return;
                }
            }

            String[] socketParts = socketAddr.split(":", 2);
            String host = socketParts[0];
            int port = Integer.parseInt(socketParts[1]);


            // Create UDP socket
            DatagramSocket socket = new DatagramSocket();




            // Create and send packet
            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, InetAddress.getByName(host), port);
            socket.send(sendPacket);
            socket.close();
        } catch (Exception e) {
            e.printStackTrace();
            // TODO Handle Exception gracefully
            System.exit(1);
        }
	}
}
