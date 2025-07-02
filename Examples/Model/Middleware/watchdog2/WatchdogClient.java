package org.robotcontrol.middleware.watchDog2;
import org.robotcontrol.middleware.idl.WatchDog;
import org.robotcontrol.middleware.rpc.RpcClient;
import org.robotcontrol.middleware.rpc.RpcValue;

public class WatchdogClient implements org.robotcontrol.middleware.idl.WatchDog{
    private RpcClient client;

    public WatchdogClient(){
        client = new RpcClient("Watchdog");
    }

    @Override
    public void heartbeat(String serviceName){
        client.invoke("heartbeat",new RpcValue.StringValue(serviceName));
    }
    
    @Override
    public void subscribe(String serviceNamePattern, String observedService){
        client.invoke("subscribe",new RpcValue.StringValue(serviceNamePattern), new RpcValue.StringValue(observedService));
    }

    
    
}
