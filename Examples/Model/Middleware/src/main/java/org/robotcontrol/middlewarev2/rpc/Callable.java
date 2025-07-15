package org.robotcontrol.middlewarev2.rpc;

import org.robotcontrol.middlewarev2.idl.types.RpcValue;

public interface Callable {
    void call(String fnName, RpcValue... args);
}
