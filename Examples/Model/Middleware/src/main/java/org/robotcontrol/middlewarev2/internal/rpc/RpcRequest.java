package org.robotcontrol.middlewarev2.internal.rpc;

import java.util.List;
import java.util.Objects;

import org.robotcontrol.middlewarev2.idl.types.RpcValue;

public class RpcRequest {
    private final Long timestamp;
    private final String function;
    private final List<RpcValue> values;

    public RpcRequest(Long timestamp, String function, List<RpcValue> values) {
        this.timestamp = timestamp;
        this.function = function;
        this.values = values;
    }

    public Long timestamp() {
        return timestamp;
    }

    public String function() {
        return function;
    }

    public List<RpcValue> values() {
        return values;
    }

    public boolean isInternal() {
        return timestamp == null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RpcRequest)) return false;
        RpcRequest that = (RpcRequest) o;
        return Objects.equals(function, that.function) &&
               Objects.equals(values, that.values);
    }

    @Override
    public int hashCode() {
        return Objects.hash(function, values);
    }

    @Override
    public String toString() {
        return "RpcRequest{" +
               "function='" + function + '\'' +
               ", values=" + values +
               '}';
    }
}
