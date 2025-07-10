package org.robotcontrol.middleware.rpc;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.robotcontrol.middleware.ServerStub_I;
import org.robotcontrol.middleware.dns.DnsClient;
import org.robotcontrol.middleware.utils.Logger;

import lombok.AllArgsConstructor;
import lombok.Data;

public class RpcServer {
    private final Logger logger = new Logger("DnsService");
    private volatile boolean running = false;
    private List<Thread> listenerThreads = new ArrayList<Thread>();
    private ConcurrentHashMap<ServerStub_I, ServiceProps> concurrentMap = new ConcurrentHashMap<>();
    // Scheduler for periodic updates
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private DnsClient dns;

    @Data
    @AllArgsConstructor
    private class ServiceProps {
        private String serviceName;
        private List<String> functionNames;
        private DatagramSocket socket;
    }

    public void addService(ServerStub_I service, String serviceName, String... fnNames) {
        concurrentMap.put(service, new ServiceProps(serviceName, Arrays.asList(fnNames), null));
    }

    public void addService(ServerStub_I service, DatagramSocket socket) {
        concurrentMap.put(service, new ServiceProps(null, null, socket));
    }

    public void Listen() {
        if (running)
            return;
        running = true;

        dns = new DnsClient();

        for (Map.Entry<ServerStub_I, ServiceProps> entry : concurrentMap.entrySet()) {
            ServerStub_I service = entry.getKey();
            ServiceProps serviceProps = entry.getValue();

            Thread listenerThread = new Thread(() -> {
                try {
                   
                    if(serviceProps.getSocket() == null){
                        serviceProps.setSocket(new DatagramSocket());
                    }
                    logger.debug("RPC server is listening on port %s/udp ...", serviceProps.getSocket().getLocalPort());

                    // if (serviceProps.getFunctionNames() != null) {
                    //     for (String fnName : serviceProps.getFunctionNames()) {
                    //         String socketAddr = getReachableLocalIp() + ":" + socket.getLocalPort();
                    //         logger.debug("Register with DNS: %s.%s -> %s", serviceProps.getServiceName(), fnName,
                    //                 socketAddr);
                    //         dns.register(serviceProps.serviceName, fnName, socketAddr);
                    //     }
                    // }
                    scheduler.scheduleAtFixedRate(this::periodicRegistration, 0, 3000, TimeUnit.MILLISECONDS);

                    byte[] buffer = new byte[256];
                    while (running) {
                        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                        serviceProps.getSocket().receive(packet); // blocking call

                        String received = new String(packet.getData(), 0, packet.getLength());
                        logger.debug("Received from %s:%s | Message: %s", packet.getAddress(), packet.getPort(),
                                received);

                        RpcRequest req = Marshaller.unmarshal(received);

                        logger.debug("calling %s", req.function());
                        service.call(req.function(), req.values().toArray(new RpcValue[0]));
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
            for (Thread listenerThread : listenerThreads) {
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
        for (Thread listenerThread : listenerThreads) {
            if (listenerThread != null) {
                listenerThread.interrupt(); // wakes up socket.receive if needed
            }
        }
    }

    // TODO move to utils
    private String getIp() {
        String envVar = System.getenv("IP_ADDR");
        if (envVar != null && !envVar.isEmpty()) {
            return envVar;
        } else {
            logger.trace("IP_ADDR undefined, defaulting to 127.0.0.1");
            return "127.0.0.1"; // fallback
        }
    }

    private void periodicRegistration() {
        for (Map.Entry<ServerStub_I, ServiceProps> entry : concurrentMap.entrySet()) {
            ServerStub_I service = entry.getKey();
            ServiceProps serviceProps = entry.getValue();
            DatagramSocket socket = serviceProps.getSocket();
                   

            if (serviceProps.getFunctionNames() != null) {
                for (String fnName : serviceProps.getFunctionNames()) {
                    String socketAddr = getIp() + ":" + socket.getLocalPort();
                    logger.debug("RPCServer: PeriodicRegistration Socket %s\n",socketAddr);
                    if (dns.resolve(serviceProps.serviceName, fnName) != socketAddr) {
                        
                        dns.register(serviceProps.serviceName, fnName, socketAddr);
                    }
                }
            }

        }
    }

}
