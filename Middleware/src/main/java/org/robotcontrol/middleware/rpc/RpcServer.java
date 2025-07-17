package org.robotcontrol.middleware.rpc;

public interface RpcServer {
    public void listenAndServe(); // blocking
    public void start();
    public void stop();
}
