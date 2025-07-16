package org.robotcontrol.middlewarev2.internal.idl;

import org.robotcontrol.middlewarev2.idl.HealthReportConsumer;
import org.robotcontrol.middlewarev2.idl.MoveAdapter;
import org.robotcontrol.middlewarev2.idl.MoveAdapter.RobotDirection;
import org.robotcontrol.middlewarev2.idl.types.RpcValue;
import org.robotcontrol.middlewarev2.internal.rpc.RpcClientImpl;
import org.robotcontrol.middlewarev2.rpc.Callable;
import org.robotcontrol.middlewarev2.rpc.Invokable;

import lombok.AllArgsConstructor;

public class HealthReportConsumerImpl {
    @AllArgsConstructor
    public static class Service implements Callable {
        private final HealthReportConsumer healthReportConsumer;

        @Override
        public void call(String fnName, RpcValue... args) {
            switch (fnName) {
                case "reportHealth":
                    healthReportConsumer.reportHealth(
                        (String) RpcValue.unwrap(args[0]), 
                        (String) RpcValue.unwrap(args[1])
                    );
                    break;
                default:
                    throw new IllegalArgumentException(String.format("Unknown function %s", fnName));
            }
        } 
    }

    public static class Client implements HealthReportConsumer {
        private Invokable client;

        public Client(String serviceName) {
            this.client = new RpcClientImpl(serviceName, false);
        }
        @Override
        public void reportHealth(String serviceName, String subscription) {
            client.invoke(
                "reportHealth", 
                new RpcValue.StringValue(serviceName),
                new RpcValue.StringValue(subscription)
            );    
        }

    }
}
