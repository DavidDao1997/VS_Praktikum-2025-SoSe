package org.robotcontrol.middleware.internal.dns;

import org.robotcontrol.middleware.internal.rpc.RawRpcServerImpl;
import org.robotcontrol.middleware.rpc.RpcServer;

public class DnsServerImpl implements RpcServer {
    private final RpcServer server;
    
    public DnsServerImpl(int port) {
        this.server = new RawRpcServerImpl(new DnsImpl(), port);
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

}
