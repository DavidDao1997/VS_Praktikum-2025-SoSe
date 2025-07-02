package org.robotcontrol.middleware.dns;

import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.robotcontrol.middleware.RpcUtils;
import org.robotcontrol.middleware.RpcValue;
import org.robotcontrol.middleware.ServerStub_I;
import org.robotcontrol.middleware.idl.DnsClientCallbackService;
import org.robotcontrol.middleware.udp.UdpClient;
import org.robotcontrol.middleware.udp.UdpServer;

public class DnsClient implements Dns {
    private UdpClient client;

    public DnsClient(String socket) {
        String[] sockerParts = socket.split(":", 2);
        client = new UdpClient(sockerParts[0], Integer.parseInt(sockerParts[1]));

        throw new UnsupportedOperationException("WIP: This class is a work in progress.");
    }
    public void register(
        String serviceName, 
        String functionName, 
        String socket
    ) {
        client.invoke(
            "register", 
            new RpcValue.StringValue(serviceName), 
            new RpcValue.StringValue(functionName), 
            new RpcValue.StringValue(socket)
        );
    }

    public String resolve(
        String serviceName, 
        String functionName
    ) { 
        CompletableFuture<String> resolutionFuture = new CompletableFuture<>();
        UdpServer cbServer = new UdpServer();
        String callbackHostport = "";
        throw new UnsupportedOperationException("WIP: This method is a work in progress.");
        // try {
        //     DatagramSocket socket;
        //     socket = new DatagramSocket();
        //     callbackHostport = getReachableLocalIp() + ":" + Integer.toString(socket.getLocalPort());

        //     // FIXME requires new feature that lets us pass a full socket definition too the server
        //     // cbServer.addService(socket, new DnsClientCallbackServiceImpl(resolutionFuture));
        // } catch (SocketException e) {
        //     // TODO Auto-generated catch block
        //     e.printStackTrace();
        // }
        cbServer.Listen();
        // wait as the server setup is not instant
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        client.invoke("resolve", new RpcValue.StringValue(serviceName), new RpcValue.StringValue(functionName), new RpcValue.StringValue(callbackHostport));

        try {
            return resolutionFuture.get(5, TimeUnit.SECONDS);
        } catch (TimeoutException | InterruptedException | ExecutionException e) {
            System.err.println("DnsClient Main: Resolution failed for " + serviceName + "." + functionName);
        }
        return "";
    }

    private class DnsClientCallbackServiceImpl implements DnsClientCallbackService, ServerStub_I {
        private CompletableFuture<String> resolutionFuture;

        private DnsClientCallbackServiceImpl(CompletableFuture<String> resolutionFuture) {
            this.resolutionFuture = resolutionFuture;
        }

        @Override
        public void call(String fnName, RpcValue... args) {
            receiveResolution((String) RpcUtils.unwrap(args[0]));
        }

        @Override
        public void receiveResolution(String resolvedSocket) {
            resolutionFuture.complete(resolvedSocket);
        }


    }

    private static String getReachableLocalIp() {
        try (DatagramSocket tmpSocket = new DatagramSocket()) {
            // Use an external IP to determine the right interface — doesn't send anything
            tmpSocket.connect(InetAddress.getByName("8.8.8.8"), 53);
            return tmpSocket.getLocalAddress().getHostAddress();
        } catch (Exception e) {
            e.printStackTrace();
            return "127.0.0.1"; // fallback
        }
    }
}