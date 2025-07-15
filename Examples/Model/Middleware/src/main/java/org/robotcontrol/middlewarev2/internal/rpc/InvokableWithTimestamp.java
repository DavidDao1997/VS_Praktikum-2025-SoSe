package org.robotcontrol.middlewarev2.internal.rpc;

import org.robotcontrol.middlewarev2.idl.types.RpcValue;

public interface InvokableWithTimestamp {
    void invoke(Long timestamp, String fnName, RpcValue... args);
}