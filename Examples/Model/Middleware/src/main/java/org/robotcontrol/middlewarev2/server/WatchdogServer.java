package org.robotcontrol.middlewarev2.server;

import org.robotcontrol.middleware.utils.Environment;
import org.robotcontrol.middlewarev2.internal.idl.WatchdogImpl;
import org.robotcontrol.middlewarev2.rpc.RpcServer;

public class WatchdogServer extends WatchdogImpl.Server implements RpcServer {
    public WatchdogServer(Integer port) {
        super(port, new WatchdogImpl.Service());
    }

    public static void main(String[] args) {
        Integer port = Environment.getEnvIntOrExit("PORT");
        new WatchdogServer(port).listenAndServe();
    }
}
