package org.robotcontrol.middleware.rpc;

import org.robotcontrol.middleware.idl.types.RpcValue;

public interface Callable {
    void call(String fnName, RpcValue... args);
}
