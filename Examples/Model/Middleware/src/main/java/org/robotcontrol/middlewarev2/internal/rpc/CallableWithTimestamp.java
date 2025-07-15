package org.robotcontrol.middlewarev2.internal.rpc;

import org.robotcontrol.middlewarev2.idl.types.RpcValue;

public interface CallableWithTimestamp {
    void call(Long timestamp, String fnName, RpcValue... args);
}

