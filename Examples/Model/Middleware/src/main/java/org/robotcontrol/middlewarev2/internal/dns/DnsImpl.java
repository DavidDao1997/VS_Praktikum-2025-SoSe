package org.robotcontrol.middlewarev2.internal.dns;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.robotcontrol.middleware.utils.Logger;
import org.robotcontrol.middlewarev2.idl.types.RpcValue;
import org.robotcontrol.middlewarev2.internal.rpc.RawRpcClientImpl;
import org.robotcontrol.middlewarev2.rpc.Callable;

class DnsImpl implements Callable {
    private final Logger logger = new Logger("DnsImpl");

    private final Map<String, String> serviceRegistry = new ConcurrentHashMap<>();

    @Override
    public void call(String fnName, RpcValue... args) {
        if (args.length != 3) {
            throw new RuntimeException("register called with " + args.length + "Arguments instead of 3");
        }
        switch (fnName) {
            case "register":
                register(
                    (String) RpcValue.unwrap(args[0]),
                    (String) RpcValue.unwrap(args[1]),
                    (String) RpcValue.unwrap(args[2])
                );
                break;
            case "resolve":
                resolve(
                    (String) RpcValue.unwrap(args[0]),
                    (String) RpcValue.unwrap(args[1]),
                    (String) RpcValue.unwrap(args[2])
                );
                break;
            default:
                throw new RuntimeException("unknown functionname: " + fnName);
        }
    }

    public void register(String serviceName, String functionName, String socket) {
        logger.debug("registered: %s.%s -> %s", serviceName, functionName, socket);
        serviceRegistry.put(toDescriptor(serviceName, functionName), socket);
    }

    public void resolve(String serviceName, String functionName, String callbackSocket) {
        String resolvedSocket = serviceRegistry.getOrDefault(toDescriptor(serviceName, functionName), "");
        if (!resolvedSocket.isEmpty()) logger.debug("resolving: %s.%s -> %s", serviceName, functionName, resolvedSocket);
        
        RawRpcClientImpl callbackClient = new RawRpcClientImpl(callbackSocket);
        callbackClient.invoke(
            "receiveResolution",
            new RpcValue.StringValue(serviceName), 
            new RpcValue.StringValue(functionName), 
            new RpcValue.StringValue(resolvedSocket)
        );
    }

    private String toDescriptor(String serviceName, String functionName) { return serviceName + "." + functionName; }
}
