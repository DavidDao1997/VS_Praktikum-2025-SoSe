package org.robotcontrol.middleware.rpc;

import org.robotcontrol.middleware.idl.types.RpcValue;

public interface Invokable {
    void invoke(String fnName, RpcValue... args);
}