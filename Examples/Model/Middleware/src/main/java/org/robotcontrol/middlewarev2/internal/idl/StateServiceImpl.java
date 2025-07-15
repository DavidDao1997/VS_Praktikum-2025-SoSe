package org.robotcontrol.middlewarev2.internal.idl;

import org.robotcontrol.middlewarev2.idl.MoveAdapter;
import org.robotcontrol.middlewarev2.idl.StateService;
import org.robotcontrol.middlewarev2.idl.StateService.SelectDirection;
import org.robotcontrol.middlewarev2.idl.types.RpcValue;
import org.robotcontrol.middlewarev2.internal.rpc.RpcClientImpl;
import org.robotcontrol.middlewarev2.internal.rpc.RpcServerImpl;
import org.robotcontrol.middlewarev2.rpc.Callable;
import org.robotcontrol.middlewarev2.rpc.Invokable;
import org.robotcontrol.middlewarev2.rpc.RpcServer;

import lombok.AllArgsConstructor;

public class StateServiceImpl {
    @AllArgsConstructor
    public static class Service implements Callable {
        private final StateService stateService;

        @Override
        public void call(String fnName, RpcValue... args) {
            switch (fnName) {
                case "select":
                    System.out.println(RpcValue.unwrap(args[0]));
                    stateService.select(SelectDirection.values()[((Long) RpcValue.unwrap(args[0])).intValue()]);    
                    break;
                // case "reportHealth":
                //     stateService.reportHealth(fnName, false);

                default:
                    break;
            }
        }
    }

    public static class Client implements StateService {
        private Invokable client;

        public Client() {
            this.client = new RpcClientImpl("StateService", false);
        }

        @Override
        public void reportHealth(String serviceName, boolean isAlive) {
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
