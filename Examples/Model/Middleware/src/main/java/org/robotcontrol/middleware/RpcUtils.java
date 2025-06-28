package org.robotcontrol.middleware;

import java.util.stream.Collectors;

public class RpcUtils {

    public static Object[] unwrap(RpcValue[] rpcArgs) {
        Object[] result = new Object[rpcArgs.length];
        for (int i = 0; i < rpcArgs.length; i++) {
            result[i] = unwrap(rpcArgs[i]);
        }
        return result;
    }

    public static Object unwrap(RpcValue rpcValue) {
        if (rpcValue instanceof RpcValue.IntValue iv) {
            return iv.value();
        } else if (rpcValue instanceof RpcValue.StringValue sv) {
            return sv.value();
        } else if (rpcValue instanceof RpcValue.BoolValue bv) {
            return bv.value();
        } else if (rpcValue instanceof RpcValue.ListValue lv) {
            // recursively unwrap the list elements
            return lv.values().stream()
                     .map(RpcUtils::unwrap)
                     .collect(Collectors.toList());
        } else {
            throw new IllegalArgumentException("Unknown RpcValue type: " + rpcValue);
        }
    }
}