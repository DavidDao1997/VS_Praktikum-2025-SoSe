package org.robotcontrol.middlewarev2.rpc;

import org.robotcontrol.middlewarev2.idl.types.RpcValue;

public interface Invokable {
    void invoke(String fnName, RpcValue... args);
}