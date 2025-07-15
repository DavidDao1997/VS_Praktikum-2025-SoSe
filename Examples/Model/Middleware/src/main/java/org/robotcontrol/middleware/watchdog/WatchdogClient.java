package org.robotcontrol.middleware.watchdog;

import org.robotcontrol.middleware.idl.WatchDog;
import org.robotcontrol.middleware.rpc.RpcClient;
import org.robotcontrol.middleware.rpc.RpcValue;
import org.robotcontrol.middleware.utils.Logger;

public class WatchdogClient implements org.robotcontrol.middleware.idl.WatchDog {
    private RpcClient client;
    Logger logger;

    public WatchdogClient(){
        client = new RpcClient("Watchdog");
        logger = new Logger("WatchdogClient");
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

