package org.robotcontrol.middlewarev2.internal.timestamp;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.robotcontrol.middlewarev2.idl.types.RpcValue;
import org.robotcontrol.middlewarev2.rpc.Callable;

public class TimestampServiceImpl implements Callable {
    private final Map<String, Long> timestampRegistry = new ConcurrentHashMap<>();

    @Override
    public void call(String fnName, RpcValue... args) {
        if (!fnName.equals("setTimestamp") || args.length != 3) { throw new IllegalArgumentException(String.format("fn: %s, argCount: %s", fnName, args.length)); }
        setTimestamp(
            (String) RpcValue.unwrap(args[0]),
            (String) RpcValue.unwrap(args[1]),
            (Long) RpcValue.unwrap(args[2])
        );
    }

    private void validate(Integer timestamp) {
        if (timestamp < 0 ) { throw new IllegalArgumentException("negative timestamp"); }
    }

    private void setTimestamp(String serviceName, String functionName, Long timestamp) {
        timestampRegistry.put(toKey(serviceName, functionName), timestamp);
    }

    public Long getTimestamp(String serviceName, String functionName) {
        return timestampRegistry.get(toKey(serviceName, functionName));
    }

    private String toKey(String serviceName, String functionName) {
        return serviceName + "." + functionName;
    } 
}
