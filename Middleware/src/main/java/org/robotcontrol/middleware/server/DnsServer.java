package org.robotcontrol.middleware.server;

import org.robotcontrol.middleware.internal.dns.DnsServerImpl;
import org.robotcontrol.middleware.rpc.RpcServer;
import org.robotcontrol.middleware.utils.Environment;

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
