package org.robotcontrol.middleware.internal.idl;

import org.robotcontrol.middleware.idl.MoveAdapter;
import org.robotcontrol.middleware.idl.StateService;
import org.robotcontrol.middleware.idl.StateService.SelectDirection;
import org.robotcontrol.middleware.idl.types.RpcValue;
import org.robotcontrol.middleware.internal.rpc.RpcClientImpl;
import org.robotcontrol.middleware.internal.rpc.RpcServerImpl;
import org.robotcontrol.middleware.rpc.Callable;
import org.robotcontrol.middleware.rpc.Invokable;
import org.robotcontrol.middleware.rpc.RpcServer;

import lombok.AllArgsConstructor;

public class StateServiceImpl {
    @AllArgsConstructor
    public static class Service implements Callable {
        private final StateService stateService;

        @Override
        public void call(String fnName, RpcValue... args) {
            switch (fnName) {
                case "select":
                    stateService.select(SelectDirection.values()[((Long) RpcValue.unwrap(args[0])).intValue()]);    
                    break;
                case "reportHealth":
                    stateService.reportHealth(
                        (String) RpcValue.unwrap(args[0]),
                        (String) RpcValue.unwrap(args[1])
                    );
                    break;
                default:
                    throw new IllegalArgumentException(String.format("Unknown function %s", fnName));
            }
        }
    }

    public static class Client implements StateService {
        private Invokable client;

        public Client() {
            this.client = new RpcClientImpl("StateService", false);
        }

        @Override
        public void reportHealth(String serviceName, String subscription) {
            // TODO Auto-generated method stub
            throw new UnsupportedOperationException("Unimplemented method 'reportHealth'");
        }

        @Override
        public void setError(boolean err, boolean confirm) {
            // TODO Auto-generated method stub
            throw new UnsupportedOperationException("Unimplemented method 'setError'");
        }

        @Override
        public void select(SelectDirection selectDirection) {
            client.invoke(
                "select", 
                new RpcValue.LongValue(selectDirection.ordinal())
            );
        } 
    }

    public static class Server extends RpcServerImpl {
        public Server(Integer port, StateService service, String[] clientNames, String serviceName, String... functionNames) {
            super(port, new Service(service), false, serviceName, functionNames);
            for (String client: clientNames) {
                for (String functionName: functionNames) {
                    super.addSubscription(client, serviceName, functionName);
                }
            }
        }
    }
    
}
