package org.robotcontrol.middleware.internal.idl;

import java.util.List;

import org.robotcontrol.middleware.idl.MoveAdapter;
import org.robotcontrol.middleware.idl.MoveAdapter.RobotDirection;
import org.robotcontrol.middleware.idl.types.RpcValue;
import org.robotcontrol.middleware.internal.rpc.RpcClientImpl;
import org.robotcontrol.middleware.internal.rpc.RpcServerImpl;
import org.robotcontrol.middleware.rpc.Callable;
import org.robotcontrol.middleware.rpc.Invokable;
import org.robotcontrol.middleware.rpc.RpcServer;

import lombok.AllArgsConstructor;

public class MoveAdapterImpl {
    @AllArgsConstructor
    public static class Service implements Callable {
        private final MoveAdapter moveAdapter;

        @Override
        public void call(String fnName, RpcValue... args) {
            switch (fnName) {
                case "move":
                    moveAdapter.move(RobotDirection.values()[((Long) RpcValue.unwrap(args[0])).intValue()]);
                    break;
                case "setSelected":
                    moveAdapter.setSelected((String) RpcValue.unwrap(args[0]));
                    break;
                default:
                    throw new IllegalArgumentException(String.format("Unknown function %s", fnName));
            }
        } 
    }

    public static class Client implements MoveAdapter {
        private Invokable client;

        public Client() {
            this.client = new RpcClientImpl("MoveAdapter", false);
        }

        @Override
        public void move(RobotDirection robotDirection) {
            client.invoke(
                "move", 
                new RpcValue.LongValue(robotDirection.ordinal())
            );
        }

        @Override
        public void setSelected(String selected) {
            client.invoke(
                "setSelected", 
                new RpcValue.StringValue(selected)
            );
        } 
    }

    public static class Server implements RpcServer {
        private RpcServerImpl server;
        public Server(Integer port, MoveAdapter service, String[] clientNames, String serviceName, String... functionNames) {
            server = new RpcServerImpl(port, new Service(service), false, serviceName, functionNames);
            for (String client: clientNames) {
                for (String functionName: functionNames) {
                    server.addSubscription(client, serviceName, functionName);
                }
            }
        }

        @Override
        public void listenAndServe() {
            server.listenAndServe();
        }

        @Override
        public void start() {
            server.start();
        }

        @Override
        public void stop() {
            server.stop();
        }
    }

}
