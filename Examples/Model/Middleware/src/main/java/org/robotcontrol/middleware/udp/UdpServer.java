package org.robotcontrol.middleware.udp;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.robotcontrol.middleware.Marshaller;
import org.robotcontrol.middleware.RpcRequest;
import org.robotcontrol.middleware.RpcValue;
import org.robotcontrol.middleware.ServerStub_I;


public class UdpServer {
    private volatile boolean running = false;
    private List<Thread> listenerThreads;
    private Map<Integer, ServerStub_I> services;

    public UdpServer() {
        this.services = new HashMap<>();
        this.listenerThreads = new ArrayList<>();
    }

    public void addService(int port, ServerStub_I service) {
        this.services.put(port, service);
    }

    public void Listen() {
        if (running) return;

        running = true;
        for (Map.Entry<Integer, ServerStub_I> entry: services.entrySet()) {
            int port = entry.getKey();
            ServerStub_I service = entry.getValue();
            Thread listenerThread = new Thread(() -> {
                byte[] buffer = new byte[256];
    
                try (DatagramSocket socket = new DatagramSocket(port)) {
                    System.out.println("UDP server is listening on port " + port + "...");
    
                    while (running) {
                        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                        socket.receive(packet); // blocking call
    
                        String received = new String(packet.getData(), 0, packet.getLength());
                        System.out.println("Received from " + packet.getAddress() + ":" + packet.getPort());
                        System.out.println("Message: " + received);
                        RpcRequest req = Marshaller.unmarshal(received);
                        
                        service.call(req.function(),req.values().toArray(new RpcValue[0]));

                    }
                } catch (Exception e) {
                    if (running) e.printStackTrace(); // suppress expected exception on shutdown
                }
    
                System.out.println("UDP server has stopped.");
            });
    
            listenerThreads.add(listenerThread);
            listenerThread.start();
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
}
