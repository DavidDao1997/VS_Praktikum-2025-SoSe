package org.robotcontrol.middleware;

import org.robotcontrol.middleware.rpc.RpcValue;

public interface ServerStub_I {
    public void call(String fnName, RpcValue... args);
}
