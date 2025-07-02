package org.robotcontrol.middleware.dns;

import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.robotcontrol.middleware.ServerStub_I;
import org.robotcontrol.middleware.idl.DnsClientCallbackService;
import org.robotcontrol.middleware.idl.DnsService;
import org.robotcontrol.middleware.rpc.RpcClient;
import org.robotcontrol.middleware.rpc.RpcServer;
import org.robotcontrol.middleware.rpc.RpcUtils;
import org.robotcontrol.middleware.rpc.RpcValue;

public class DnsServiceImpl implements DnsService, ServerStub_I {
    private final Map<String, String> serviceRegistry = new ConcurrentHashMap<>();

    @Override
    public void register(String serviceName, String functionName, String socket) {
        String descriptor = getDescriptor(serviceName, functionName);
        serviceRegistry.put(descriptor, socket);
        System.out.println("DnsService: Registered '" + descriptor + "' -> '" + socket + "'");
    }

    @Override
    public void resolve(String serviceName, String functionName, String clientCallbackSocket) {
        String descriptor = getDescriptor(serviceName, functionName);
        String resolvedSocket = serviceRegistry.getOrDefault(descriptor, "");
        System.out.println("DnsService: Resolved '" + serviceName + "' to '" + resolvedSocket + "'. Notifying client at " + clientCallbackSocket);
        
        CallbackClient c = new CallbackClient(clientCallbackSocket);
        c.receiveResolution(resolvedSocket);
        System.out.println("DnsService: Notified client at " + clientCallbackSocket);
    }

    private String getDescriptor(String serviceName, String functionName) {
        return serviceName + "." + functionName;
    }

    @Override
    public void call(String fnName, RpcValue... args) {
        switch (fnName) {
            case "register":
                register(
                    (String) RpcUtils.unwrap(args[0]), 
                    (String) RpcUtils.unwrap(args[1]), 
                    (String) RpcUtils.unwrap(args[2])
                );
                break;
            case "resolve":
                resolve(
                    (String) RpcUtils.unwrap(args[0]), 
                    (String) RpcUtils.unwrap(args[1]), 
                    (String) RpcUtils.unwrap(args[2])
                );
            default:
                break;
        }
    }

    private class CallbackClient implements DnsClientCallbackService {
        private RpcClient client;
        private CallbackClient(String socket) {
            String[] sockerParts = socket.split(":", 2);
            // FIXME add constructor or use existing one
            // client = new UdpClient(sockerParts[0], Integer.parseInt(sockerParts[1]));
            throw new UnsupportedOperationException("WIP: This class is a work in progress.");
        }

        @Override
        public void receiveResolution(String resolvedSocket) {
            client.invoke("receiveResolution", new RpcValue.StringValue(resolvedSocket));
        }
    }

    public static void main(String[] args) throws InterruptedException, SocketException {
        int dnsServerPort = 9000; // Fixed port for DNS server

        DnsServiceImpl dnsService = new DnsServiceImpl();
        RpcServer s = new RpcServer();

        DatagramSocket socket = new DatagramSocket(dnsServerPort);
        s.addService(dnsService, socket) ;

        System.out.println("Starting DnsService on " + dnsServerPort + "...");
        s.Listen();

        // Keep the server running
        // In a real application, you'd use a robust way to keep it alive
        s.awaitTermination();

        System.out.println("DnsService shut down.");
    }
}
