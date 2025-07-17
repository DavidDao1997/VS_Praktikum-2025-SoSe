package org.robotcontrol.middlewarev2.internal.idl;

import org.robotcontrol.middleware.utils.Logger;
import org.robotcontrol.middlewarev2.idl.UI;
import org.robotcontrol.middlewarev2.idl.types.RpcValue;
import org.robotcontrol.middlewarev2.internal.Helper;
import org.robotcontrol.middlewarev2.internal.rpc.RpcClientImpl;
import org.robotcontrol.middlewarev2.internal.rpc.RpcServerImpl;
import org.robotcontrol.middlewarev2.rpc.Callable;
import org.robotcontrol.middlewarev2.rpc.Invokable;

import lombok.AllArgsConstructor;

public class UIImpl {
    private static final Logger logger = new Logger("UIImpl");
    
    @AllArgsConstructor
    public static class Service implements Callable {
        private final UI ui;

        @Override
        public void call(String fnName, RpcValue... args) {
            switch (fnName) {
                case "updateView":
                    ui.updateView(
                        Helper.convertBitmap256ToStringArray((byte[]) RpcValue.unwrap(args[0])),
                        ((Long) RpcValue.unwrap(args[1])).intValue(),
                        (boolean) RpcValue.unwrap(args[2]),
                        (boolean) RpcValue.unwrap(args[3]));
                    break;
                default:
                    throw new IllegalArgumentException(String.format("Unknown function %s", fnName));
            }
        }
    }

    public static class Client implements UI {

        private Invokable client;

        public Client(String serviceName) {
            this.client = new RpcClientImpl(serviceName, false);
        }

        @Override
        public void updateView(String[] robotBitmap, int selected, boolean error, boolean confirm) {
            client.invoke(
                "updateView",
                new RpcValue.Bitmap256Value(Helper.convertStringArrayToBitmap256(robotBitmap)),
                new RpcValue.LongValue(selected),
                new RpcValue.BoolValue(error),
                new RpcValue.BoolValue(confirm)
            );
        }
    }

    public static class Server extends RpcServerImpl {
        public Server(Integer port, UI service, String[] clientNames, String serviceName,
                String... functionNames) {
            super(port, new Service(service), false, serviceName, functionNames);
            for (String client : clientNames) {
                for (String functionName : functionNames) {
                    super.addSubscription(client, serviceName, functionName);
                }
            }
        }
    }
}
