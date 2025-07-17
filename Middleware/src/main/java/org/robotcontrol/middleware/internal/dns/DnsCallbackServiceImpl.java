package org.robotcontrol.middleware.internal.dns;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.robotcontrol.middleware.idl.types.RpcValue;
import org.robotcontrol.middleware.rpc.Callable;

public class DnsCallbackServiceImpl implements Callable {
    // private static final Logger logger = new Logger("DnsCallbackServerImpl");
    private final ConcurrentMap<String, CompletableFuture<String>> futureMap = new ConcurrentHashMap<>();

    public void registerFuture(String serviceName, String fnName, CompletableFuture<String> future) {
        futureMap.put(toKey(serviceName, fnName), future);
    }

    @Override
    public void call(String fnName, RpcValue... args) {
        receiveResolution(
            (String) RpcValue.unwrap(args[0]), 
            (String) RpcValue.unwrap(args[1]), 
            (String) RpcValue.unwrap(args[2])
        );
    }

    private void receiveResolution(String serviceName, String fnName, String resolvedSocket) {
        CompletableFuture<String> future = futureMap.remove(toKey(serviceName, fnName));
        if (future != null) {
            future.complete(resolvedSocket);
        }
    }
    
    private String toKey(String serviceName, String fnName) {
        return serviceName + "." + fnName;
    }
}
