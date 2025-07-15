package org.robotcontrol.middlewarev2.rpc;

public interface RpcServer {
    public void listenAndServe(); // blocking
    public void start();
    public void stop();
}
