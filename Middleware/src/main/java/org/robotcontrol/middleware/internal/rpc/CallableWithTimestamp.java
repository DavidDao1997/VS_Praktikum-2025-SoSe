package org.robotcontrol.middleware.internal.rpc;

import org.robotcontrol.middleware.idl.types.RpcValue;

public interface CallableWithTimestamp {
    void call(Long timestamp, String fnName, RpcValue... args);
}

