package org.robotcontrol.middleware;

public interface ClientStub_I {
    public void invoke(String fnName, RpcValue... args);
}
