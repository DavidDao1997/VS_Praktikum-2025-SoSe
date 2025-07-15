package org.robotcontrol.middlewarev2.internal.rpc;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.robotcontrol.middleware.utils.Environment;
import org.robotcontrol.middlewarev2.internal.dns.Dns;
import org.robotcontrol.middlewarev2.internal.dns.DnsClientImpl;
import org.robotcontrol.middlewarev2.internal.timestamp.TimestampClientImpl;
import org.robotcontrol.middlewarev2.rpc.Callable;
import org.robotcontrol.middlewarev2.rpc.RpcServer;

public class RpcServerImpl implements RpcServer {
    private RawRpcServerImpl rawServer;
    private Thread serverThread;

    private String socket;
    private String serviceName;
    private List<String> functionNames;
    private final Set<Subscription> timestampSubscriptions = ConcurrentHashMap.newKeySet();

    private final TimestampClientImpl timestampClientImpl;
    private Dns dns = new DnsClientImpl(); 
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    public RpcServerImpl(Integer port, Callable service, boolean isInternal, String serviceName, String... functionNames) {
        if (isInternal) {
            timestampClientImpl = null;
            rawServer = new RawRpcServerImpl(service, port);
        } else {
            timestampClientImpl = new TimestampClientImpl();
            rawServer = new RawRpcServerImpl(new CallableWithTimestampAdapter(service), port);
        }
        this.socket = Environment.getEnvStringOrExit("IP_ADDR") + ":" + port.toString();
        this.serviceName = serviceName;
        this.functionNames = Arrays.asList(functionNames);
    }

    @Override
    public void listenAndServe() {
        if (timestampClientImpl != null) scheduler.scheduleAtFixedRate(this::periodicSetTimestamp, 500, 50, TimeUnit.MILLISECONDS);
        scheduler.scheduleAtFixedRate(this::periodicRegistration, 5, 10, TimeUnit.SECONDS);
        rawServer.listenAndServe();
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
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'stop'");
    }

    public void addSubscription(String client, String serviceName, String functionName) {
        Subscription sub = new Subscription(client, serviceName, functionName);
        timestampSubscriptions.add(sub);
    }

    private void periodicRegistration() {
        for (String functionName : functionNames) {
            String resolvedSocket = dns.resolve(serviceName, functionName);
            if (!socket.equals(resolvedSocket)) {
                dns.register(serviceName, functionName, socket);
            }
        }
    }

    private void periodicSetTimestamp() {
        for (Subscription sub: timestampSubscriptions) {
            timestampClientImpl.setTimestamps(sub.client ,sub.serviceName, sub.functionName);            
        }
    }

    public class Subscription {
        private final String client;
        private final String serviceName;
        private final String functionName;

        public Subscription(String client, String serviceName, String functionName) {
            this.client = client;
            this.serviceName = serviceName;
            this.functionName = functionName;
        }

        // Required for correct Set behavior
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Subscription)) return false;
            Subscription that = (Subscription) o;
            return Objects.equals(client, that.client) &&
                Objects.equals(serviceName, that.serviceName) &&
                Objects.equals(functionName, that.functionName);
        }

        @Override
        public int hashCode() {
            return Objects.hash(client, serviceName, functionName);
        }

        @Override
        public String toString() {
            return "Subscription{" +
                "client='" + client + '\'' +
                ", serviceName='" + serviceName + '\'' +
                ", functionName='" + functionName + '\'' +
                '}';
        }
    }

}
