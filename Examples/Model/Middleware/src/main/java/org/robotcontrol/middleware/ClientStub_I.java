package org.robotcontrol.middleware;

import org.robotcontrol.middleware.rpc.RpcValue;

public interface ClientStub_I {
    public void invoke(String fnName, RpcValue... args);
}
