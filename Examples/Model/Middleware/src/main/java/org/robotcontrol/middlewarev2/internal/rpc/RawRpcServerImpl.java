package org.robotcontrol.middlewarev2.internal.rpc;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import org.robotcontrol.middleware.utils.Logger;
import org.robotcontrol.middlewarev2.idl.types.RpcValue;
import org.robotcontrol.middlewarev2.rpc.Callable;
import org.robotcontrol.middlewarev2.rpc.RpcServer;

public class RawRpcServerImpl implements RpcServer {
    private final Logger logger = new Logger("RawRpcServerImpl");
    private final Callable internalService;
    private final CallableWithTimestamp service;
    private final int port;

    private volatile boolean running = false;
    private DatagramSocket socket;
    private Thread serverThread;

    public RawRpcServerImpl(Callable internalService, int port) {
        this.internalService = internalService;
        this.service = null;
        this.port = port;
    }

    public RawRpcServerImpl(CallableWithTimestamp service, int port) {
        this.internalService = null;
        this.service = service;
        this.port = port;
    }

    @Override
    public void listenAndServe() {
        try {
            socket = new DatagramSocket(port);
            running = true;
            byte[] buffer = new byte[256];
            logger.info("Listening on port %s", port);
            while (running) {
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet); // blocking

                RpcRequest req = Marshaller.unmarshal(
                    new String(packet.getData(), 0, packet.getLength())
                );
                if (req.isInternal()) {
                    if (service != null) { 
                        logger.error("service called without timestamp"); 
                    }
                    internalService.call(req.function(), req.values().toArray(new RpcValue[0]));
                } else {
                    if (internalService != null) { 
                        logger.error("internalService called with timestamp"); 
                    }
                    service.call(req.timestamp(), req.function(), req.values().toArray(new RpcValue[0]));
                }
            }
        } catch (IOException e) {
            if (running) {
                logger.debug("IOException in listenAndServe: %s", e);
                throw new RuntimeException(e);
            } else {
                logger.info("Server stopped.");
            }
        } finally {
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
            running = false;
        }
    }

    @Override
    public void start() {
        if (serverThread != null && serverThread.isAlive()) {
            throw new IllegalStateException("Server already started");
        }
        serverThread = new Thread(this::listenAndServe);
        serverThread.start();
    }

    @Override
    public void stop() {
        running = false;
        if (socket != null && !socket.isClosed()) {
            socket.close(); // this unblocks receive()
        }
        try {
            if (serverThread != null) {
                serverThread.join(1000); // wait for shutdown
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

}
