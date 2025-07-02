package org.robotcontrol.middleware.rpc;

import java.util.ArrayList;
import java.util.List;

public class RpcUtils {

    public static Object[] unwrap(RpcValue[] rpcArgs) {
        Object[] result = new Object[rpcArgs.length];
        for (int i = 0; i < rpcArgs.length; i++) {
            result[i] = unwrap(rpcArgs[i]);
        }
        return result;
    }

    public static Object unwrap(RpcValue rpcValue) {
        if (rpcValue instanceof RpcValue.IntValue) {
            RpcValue.IntValue iv = (RpcValue.IntValue) rpcValue;
            return iv.getValue();
        } else if (rpcValue instanceof RpcValue.StringValue) {
            RpcValue.StringValue sv = (RpcValue.StringValue) rpcValue;
            return sv.getValue();
        } else if (rpcValue instanceof RpcValue.BoolValue) {
            RpcValue.BoolValue bv = (RpcValue.BoolValue) rpcValue;
            return bv.getValue();
        } else if (rpcValue instanceof RpcValue.ListValue) {
            RpcValue.ListValue lv = (RpcValue.ListValue) rpcValue;
            List<RpcValue> values = lv.getValues();
            List<Object> unwrapped = new ArrayList<>();
            for (RpcValue val : values) {
                unwrapped.add(unwrap(val));
            }
            return unwrapped;
        } else {
            throw new IllegalArgumentException("Unknown RpcValue type: " + rpcValue);
        }
    }

}