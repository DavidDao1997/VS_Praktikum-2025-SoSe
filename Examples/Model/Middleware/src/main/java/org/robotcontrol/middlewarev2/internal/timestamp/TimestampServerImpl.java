package org.robotcontrol.middlewarev2.internal.timestamp;

import org.robotcontrol.middleware.utils.Environment;
import org.robotcontrol.middlewarev2.internal.rpc.RpcServerImpl;
import org.robotcontrol.middlewarev2.rpc.RpcServer;

public class TimestampServerImpl implements RpcServer {
    private TimestampServiceImpl service;
    private RpcServerImpl server;

    private static final Object LOCK = new Object();
    private static volatile TimestampServiceImpl SERVICE_INSTANCE;
    private static volatile TimestampServerImpl SERVER_INSTANCE;
    public static TimestampServerImpl getInstance() {
        if (SERVER_INSTANCE == null || SERVICE_INSTANCE == null) {
            synchronized (LOCK) {
                if (SERVER_INSTANCE == null || SERVICE_INSTANCE == null) {
                    SERVICE_INSTANCE = new TimestampServiceImpl();
                    SERVER_INSTANCE = new TimestampServerImpl(
                        Environment.getEnvIntOrExit("TIMESTAMP_PORT"), 
                        SERVICE_INSTANCE,
                        Environment.getEnvStringOrExit("SERVICE")
                    );
                    SERVER_INSTANCE.start();
                    Runtime.getRuntime().addShutdownHook(new Thread(() -> SERVER_INSTANCE.stop()));
                }
            }
        }

        return SERVER_INSTANCE;
    }

    private TimestampServerImpl(Integer port, TimestampServiceImpl service, String serviceName, String... clients) {
        this.service = service;
        // PASSING clients makes the service external!
        server = new RpcServerImpl(port, service, true, serviceName, "setTimestamp");
    }

    @Override
    public void listenAndServe() {
        server.listenAndServe();
    }

    @Override
    public void start() {
        server.start();
    }

    @Override
    public void stop() {
        server.stop();
    }
    
    public Long getTimestamp(String serviceName, String functionName) {
        return service.getTimestamp(serviceName, functionName);
    }

    public void addSubscription(String client, String serviceName, String functionName) {
        server.addSubscription(client, serviceName, functionName);
    }
}
