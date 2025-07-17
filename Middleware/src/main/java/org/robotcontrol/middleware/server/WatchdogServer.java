package org.robotcontrol.middleware.server;

import org.robotcontrol.middleware.internal.idl.WatchdogImpl;
import org.robotcontrol.middleware.rpc.RpcServer;
import org.robotcontrol.middleware.utils.Environment;

public class WatchdogServer extends WatchdogImpl.Server implements RpcServer {
    public WatchdogServer(Integer port) {
        super(port, new WatchdogImpl.Service());
    }

    public static void main(String[] args) {
        Integer port = Environment.getEnvIntOrExit("PORT");
        new WatchdogServer(port).listenAndServe();
    }
}
