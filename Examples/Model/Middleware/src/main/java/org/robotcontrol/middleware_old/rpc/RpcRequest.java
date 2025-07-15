package org.robotcontrol.middleware.rpc;

import java.util.List;
import java.util.Objects;

public class RpcRequest {
    private final String function;
    private final List<RpcValue> values;

    public RpcRequest(String function, List<RpcValue> values) {
        this.function = function;
        this.values = values;
    }

    public String function() {
        return function;
    }

    public List<RpcValue> values() {
        return values;
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
