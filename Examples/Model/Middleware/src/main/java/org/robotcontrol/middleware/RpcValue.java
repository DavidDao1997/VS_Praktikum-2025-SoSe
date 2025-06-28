package org.robotcontrol.middleware;

import java.util.List;

public interface RpcValue {
    record IntValue(int value) implements RpcValue {}
    record StringValue(String value) implements RpcValue {}
    record BoolValue(boolean value) implements RpcValue {}
    record ListValue(List<RpcValue> values) implements RpcValue {}
}