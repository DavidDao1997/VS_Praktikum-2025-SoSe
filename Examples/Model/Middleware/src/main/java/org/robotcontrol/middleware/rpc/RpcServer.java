package org.robotcontrol.middleware.rpc;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.robotcontrol.middleware.ServerStub_I;
import org.robotcontrol.middleware.dns.DnsClient;
import org.robotcontrol.middleware.utils.Logger;

import lombok.Data;

public class RpcServer {
    private final Logger logger = new Logger("DnsService");
    private volatile boolean running = false;
    private List<Thread> listenerThreads = new ArrayList<Thread>();
    private ConcurrentHashMap<ServerStub_I, ServiceProps> concurrentMap = new ConcurrentHashMap<>();

    @Data
    private class ServiceProps {
        private final String serviceName;
        private final List<String> functionNames;
        private final DatagramSocket socket;
    }

    public void addService(ServerStub_I service, String serviceName, String ...fnNames) {
        concurrentMap.put(service, new ServiceProps(serviceName, Arrays.asList(fnNames), null));
    }

    public void addService(ServerStub_I service, DatagramSocket socket) {
        concurrentMap.put(service, new ServiceProps(null, null, socket));
    }

    public void Listen() {
        if (running) return;
        running = true;

        DnsClient dns = new DnsClient();

        for (Map.Entry<ServerStub_I, ServiceProps> entry: concurrentMap.entrySet()) {
            ServerStub_I service = entry.getKey();
            ServiceProps serviceProps = entry.getValue();
            
            Thread listenerThread = new Thread(() -> {
                try {
                    DatagramSocket socket = serviceProps.getSocket() != null 
                        ? serviceProps.getSocket()
                        : new DatagramSocket();
                    
                    logger.debug("RPC server is listening on port {}/udp ...", socket.getLocalPort());
                    
                    if (serviceProps.getFunctionNames() != null) {
                        for (String fnName: serviceProps.getFunctionNames()) {
                            String socketAddr = getReachableLocalIp() + ":" + socket.getLocalPort();
                            logger.debug("Register with DNS: {}.{} -> {}", serviceProps.getServiceName(), fnName, socketAddr);
                            dns.ensureRegister(serviceProps.getServiceName(), fnName, socketAddr);
                        }
                    }

                    byte[] buffer = new byte[256];
                    while (running) {
                        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                        socket.receive(packet); // blocking call

                        String received = new String(packet.getData(), 0, packet.getLength());
                        logger.debug("Received from {}:{} | Message: {}", packet.getAddress(), packet.getPort(), received);

                        RpcRequest req = Marshaller.unmarshal(received);
                        
                        logger.debug("calling {}", req.function());
                        service.call(req.function(),req.values().toArray(new RpcValue[0]));
                    }

                    System.out.println("RPC server has stopped.");
                } catch (Exception e) {
                    // if (running) e.printStackTrace(); // suppress expected exception on shutdown
                    e.printStackTrace();
                    // TODO Handle Exception gracefully
                    System.exit(1);
                }
            });
            listenerThread.start();
            listenerThreads.add(listenerThread);
        }
    }

    public void awaitTermination() {
        try {
            for (Thread listenerThread: listenerThreads) {
                if (listenerThread != null) {
                    listenerThread.join();
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("Interrupted while waiting for server termination");
        }
    }

    public void stop() {
        running = false;
        for (Thread listenerThread: listenerThreads) {
            if (listenerThread != null) {
                listenerThread.interrupt(); // wakes up socket.receive if needed
            }
        }
    }

    private static String getReachableLocalIp() {
        try (DatagramSocket tmpSocket = new DatagramSocket()) {
            // Use an external IP to determine the right interface — doesn't send anything
            tmpSocket.connect(InetAddress.getByName("8.8.8.8"), 53);
            return tmpSocket.getLocalAddress().getHostAddress();
        } catch (Exception e) {
            e.printStackTrace();
            // TODO Handle Exception gracefully
            System.exit(1);
            return "";
        }
    }
}
