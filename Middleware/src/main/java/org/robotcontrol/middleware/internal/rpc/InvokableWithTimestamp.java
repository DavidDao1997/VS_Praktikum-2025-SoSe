package org.robotcontrol.middleware.internal.rpc;

import org.robotcontrol.middleware.idl.types.RpcValue;

public interface InvokableWithTimestamp {
    void invoke(Long timestamp, String fnName, RpcValue... args);
}