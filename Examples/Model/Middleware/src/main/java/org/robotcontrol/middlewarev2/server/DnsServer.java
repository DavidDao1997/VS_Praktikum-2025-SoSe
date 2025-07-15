package org.robotcontrol.middlewarev2.server;

import org.robotcontrol.middleware.utils.Environment;
import org.robotcontrol.middlewarev2.internal.dns.DnsServerImpl;
import org.robotcontrol.middlewarev2.rpc.RpcServer;

public class DnsServer implements RpcServer {
    private final RpcServer server;
    public DnsServer(int port) {
        server = new DnsServerImpl(port);
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

    public static void main(String[] args) {
        Integer port = Environment.getEnvIntOrExit("PORT");
        new DnsServer(port).listenAndServe();
    }
    
}
