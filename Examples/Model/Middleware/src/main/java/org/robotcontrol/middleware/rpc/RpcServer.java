package org.robotcontrol.middleware.rpc;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.robotcontrol.middleware.ServerStub_I;

public class RpcServer {
    private volatile boolean running = false;
    private List<Thread> listenerThreads;
    private ConcurrentHashMap<K, V> concurrentMap = new ConcurrentHashMap<>();


    public void addService(ServerStub_I service, String serviceName, String ...fnNames) {

    }
}
