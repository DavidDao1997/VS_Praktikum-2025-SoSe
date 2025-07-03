package org.robotcontrol.middleware.dns;

import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.robotcontrol.middleware.ServerStub_I;
import org.robotcontrol.middleware.idl.DnsClientCallbackService;
import org.robotcontrol.middleware.rpc.RpcClient;
import org.robotcontrol.middleware.rpc.RpcServer;
import org.robotcontrol.middleware.rpc.RpcUtils;
import org.robotcontrol.middleware.rpc.RpcValue;

public class DnsClient implements Dns {
    private RpcClient client;

    public DnsClient() {
        // FIXME hardcode DNS server socket in a constant somewhere
        this("localhost:9000");
    }

    public DnsClient(String socket) {
        String[] socketParts = socket.split(":", 2);
        client = new RpcClient(socketParts[0], Integer.parseInt(socketParts[1]));
    }

    public void ensureRegister(
        String serviceName, 
        String functionName, 
        String socket
    ) {
        register(serviceName, functionName, socket);
        // TODO call resolve and reRegister in case the resolve fails  
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
        RpcServer cbServer = new RpcServer();
        String callbackHostport = "";
        try {
            DatagramSocket socket;
            socket = new DatagramSocket();
            callbackHostport = getReachableLocalIp() + ":" + Integer.toString(socket.getLocalPort());

            // FIXME requires new feature that lets us pass a full socket definition too the server
            cbServer.addService(new DnsClientCallbackServiceImpl(resolutionFuture), socket);
        } catch (SocketException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
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