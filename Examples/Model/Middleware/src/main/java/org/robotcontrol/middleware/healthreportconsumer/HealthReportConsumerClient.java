package org.robotcontrol.middleware.healthreportconsumer;

import org.robotcontrol.middleware.idl.HealthReportConsumer;
import org.robotcontrol.middleware.idl.StateService;
import org.robotcontrol.middleware.rpc.RpcClient;
import org.robotcontrol.middleware.rpc.RpcValue;

public class HealthReportConsumerClient implements HealthReportConsumer {
    private RpcClient client;

    public HealthReportConsumerClient(String serviceName) {
        client = new RpcClient(serviceName);
    }

    @Override
       public void reportHealth(String serviceName, String subscription){
        client.invoke("reportHealth", new RpcValue.StringValue(serviceName), new RpcValue.StringValue(subscription));
    }

   
    
}

