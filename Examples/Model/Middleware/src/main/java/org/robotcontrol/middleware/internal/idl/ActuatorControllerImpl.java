package org.robotcontrol.middleware.internal.idl;


import org.robotcontrol.middleware.idl.ActuatorController;
import org.robotcontrol.middleware.idl.types.RpcValue;
import org.robotcontrol.middleware.internal.rpc.RpcClientImpl;
import org.robotcontrol.middleware.internal.rpc.RpcServerImpl;
import org.robotcontrol.middleware.rpc.Callable;
import org.robotcontrol.middleware.rpc.Invokable;
import org.robotcontrol.middleware.utils.Logger;

import lombok.AllArgsConstructor;

public class ActuatorControllerImpl {
    private static final Logger logger = new Logger("ActuatorControllerImpl");

    @AllArgsConstructor
    public static class Service implements Callable {
        private final ActuatorController actuatorController;

        @Override
        public void call(String fnName, RpcValue... args) {
            switch (fnName) {
                case "move":
                    actuatorController.move(ActuatorController.Direction.values()[((Long) RpcValue.unwrap(args[0])).intValue()]);
                    break;
                default:
                    throw new IllegalArgumentException(String.format("Unknown function %s", fnName));
            }
        }
    }

    public static class Client implements ActuatorController {
         private Invokable client;

        public Client(String serviceName) {
            this.client = new RpcClientImpl(serviceName, false);
        }

        @Override
        public void move(Direction actuatorDirection) {
            logger.debug("invoking move");
            client.invoke(
                "move",
                new RpcValue.LongValue(actuatorDirection.ordinal())
            );
        }
    }

    public static class Server extends RpcServerImpl {
        public Server(Integer port, ActuatorController service, String[] clientNames, String serviceName, String... functionNames) {
            super(port, new Service(service), false, serviceName, functionNames);
            for (String client: clientNames) {
                for (String functionName: functionNames) {
                    super.addSubscription(client, serviceName, functionName);
                }
            }
        }
    } 
}
