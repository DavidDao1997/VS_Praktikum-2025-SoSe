package org.robotcontrol.middlewarev2.internal.dns;

import org.robotcontrol.middlewarev2.internal.rpc.RawRpcServerImpl;
import org.robotcontrol.middlewarev2.rpc.RpcServer;

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
