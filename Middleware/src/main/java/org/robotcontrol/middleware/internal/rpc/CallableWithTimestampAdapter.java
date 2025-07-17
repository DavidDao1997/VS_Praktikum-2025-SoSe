package org.robotcontrol.middleware.internal.rpc;

import org.robotcontrol.middleware.idl.types.RpcValue;
import org.robotcontrol.middleware.rpc.Callable;
import org.robotcontrol.middleware.utils.Logger;

public class CallableWithTimestampAdapter implements CallableWithTimestamp {
    private static final Logger logger = new Logger("CallableWithTimestampAdapter");
    private final Callable delegate;


    public CallableWithTimestampAdapter(Callable delegate) {
        this.delegate = delegate;
    }

    @Override
    public void call(Long timestamp, String fnName, RpcValue... args) {
        Long timeLeft = timestamp + 100 - (System.currentTimeMillis() & 0xFFFFFFFFL);
        if (timeLeft >= 0) {
            delegate.call(fnName, args);
        } else {
            logger.debug("Dropped call %s time left: %s", fnName, timeLeft);
        }; 
    }
}
