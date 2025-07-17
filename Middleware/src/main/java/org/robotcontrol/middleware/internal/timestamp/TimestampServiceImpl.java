package org.robotcontrol.middleware.internal.timestamp;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.robotcontrol.middleware.idl.types.RpcValue;
import org.robotcontrol.middleware.rpc.Callable;
import org.robotcontrol.middleware.utils.Logger;

public class TimestampServiceImpl implements Callable {
    private static final Logger logger = new Logger("TimestampServiceImpl");
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
        logger.trace("setting Timestamp: %s.%s -> %s",serviceName, functionName, timestamp);
        timestampRegistry.put(toKey(serviceName, functionName), timestamp);
    }

    public Long getTimestamp(String serviceName, String functionName) {
        logger.debug("getting Timestamp: %s.%s -> %s",serviceName, functionName, timestampRegistry.get(toKey(serviceName, functionName)));
        return timestampRegistry.get(toKey(serviceName, functionName));
    }

    private String toKey(String serviceName, String functionName) {
        return serviceName + "." + functionName;
    } 
}
