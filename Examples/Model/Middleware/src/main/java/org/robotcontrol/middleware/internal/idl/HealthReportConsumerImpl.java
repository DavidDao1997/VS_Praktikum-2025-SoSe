package org.robotcontrol.middleware.internal.idl;

import org.robotcontrol.middleware.idl.HealthReportConsumer;
import org.robotcontrol.middleware.idl.MoveAdapter;
import org.robotcontrol.middleware.idl.MoveAdapter.RobotDirection;
import org.robotcontrol.middleware.idl.types.RpcValue;
import org.robotcontrol.middleware.internal.rpc.RpcClientImpl;
import org.robotcontrol.middleware.rpc.Callable;
import org.robotcontrol.middleware.rpc.Invokable;

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
