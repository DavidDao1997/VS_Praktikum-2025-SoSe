package org.robotcontrol.middlewarev2.internal.rpc;

import org.robotcontrol.middlewarev2.idl.types.RpcValue;
import org.robotcontrol.middlewarev2.internal.timestamp.TimestampServerImpl;
import org.robotcontrol.middlewarev2.rpc.Invokable;

public class InvokableWithTimestampAdapter implements Invokable {
    private final InvokableWithTimestamp delegate;
    private final String serviceName;
    private final TimestampServerImpl timestampServerImpl = TimestampServerImpl.getInstance();
    
    public InvokableWithTimestampAdapter(InvokableWithTimestamp delegate, String serviceName) {
        this.delegate = delegate;
        this.serviceName = serviceName;
    }

    @Override
    public void invoke(String fnName, RpcValue... args) {
        Long timestamp = timestampServerImpl.getTimestamp(serviceName, fnName);
        // System.out.printf("INVOKE WITH TIMESTAMP %s, SERVICE %s, fn %s\n", timestamp, serviceName, fnName);
        if (timestamp != null) {
            delegate.invoke(timestamp, fnName, args);
        }
    }
}
