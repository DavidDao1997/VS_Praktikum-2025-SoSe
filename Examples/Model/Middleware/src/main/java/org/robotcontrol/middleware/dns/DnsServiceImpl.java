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
import org.robotcontrol.middleware.utils.Logger;


public class DnsServiceImpl implements DnsService, ServerStub_I {
    private final Logger logger = new Logger("DnsService");

    private final Map<String, String> serviceRegistry = new ConcurrentHashMap<>();

    @Override
    public void register(String serviceName, String functionName, String socket) {
        String descriptor = getDescriptor(serviceName, functionName);
        serviceRegistry.put(descriptor, socket);
        logger.info("Registered '%s'' -> '%s'", descriptor, socket);
    }

    @Override
    public void resolve(String serviceName, String functionName, String clientCallbackSocket) {
        String descriptor = getDescriptor(serviceName, functionName);
        String resolvedSocket = serviceRegistry.getOrDefault(descriptor, "");
        logger.debug("Resolved '%s'", serviceName);
        logger.debug("Resolved '%s' to '%s'. Notifying client at %s", serviceName, resolvedSocket, clientCallbackSocket);
                
        CallbackClient c = new CallbackClient(clientCallbackSocket);
        c.receiveResolution(serviceName, functionName, resolvedSocket);
        logger.debug("Notified client at %s", clientCallbackSocket);
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
            String[] socketParts = socket.split(":", 2);
            client = new RpcClient(socketParts[0], Integer.parseInt(socketParts[1]));
        }

        @Override
        public void receiveResolution(String serviceName, String fnName, String resolvedSocket) {
            client.invoke("receiveResolution", new RpcValue.StringValue(serviceName), new RpcValue.StringValue(fnName), new RpcValue.StringValue(resolvedSocket));
        }
    }

    public static void main(String[] args) throws InterruptedException, SocketException {
        Logger logger = new Logger("DnsService");
        // logger.trace("this is trace logs");
        // logger.debug("this is debug logs");
        // logger.info("this is info logs");
        // logger.warn("this is warn logs");
        int dnsServerPort = 9000; // Fixed port for DNS server

        DnsServiceImpl dnsService = new DnsServiceImpl();
        RpcServer s = new RpcServer();

        DatagramSocket socket = new DatagramSocket(dnsServerPort);
        s.addService(dnsService, socket) ;

        logger.info("Starting DnsService on port %s", dnsServerPort);
        s.Listen();

        // Keep the server running
        // In a real application, you'd use a robust way to keep it alive
        s.awaitTermination();

        logger.info("DnsService shut down.");
    }
}
