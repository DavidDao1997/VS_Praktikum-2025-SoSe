package org.robotcontrol.middleware.watchdog;

import org.robotcontrol.middleware.idl.WatchDog;
import org.robotcontrol.middleware.rpc.RpcClient;
import org.robotcontrol.middleware.rpc.RpcValue;

public class WatchdogClient implements org.robotcontrol.middleware.idl.WatchDog {
    private RpcClient client;

    public WatchdogClient(){
        client = new RpcClient("Watchdog");
    }

    @Override
    public void heartbeat(String serviceName){
        client.invoke("heartbeat",new RpcValue.StringValue(serviceName));
    }
    
    @Override
    public void subscribe(String serviceName, String patternStr){
        client.invoke("subscribe",new RpcValue.StringValue(serviceName), new RpcValue.StringValue(patternStr));
    }

    
    
}

