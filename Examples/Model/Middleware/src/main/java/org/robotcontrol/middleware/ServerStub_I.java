package org.robotcontrol.middleware;

public interface ServerStub_I {
    public void call(String fnName, RpcValue... args);
}
