package org.robotcontrol.middleware.watchdog;
import org.robotcontrol.middleware.ServerStub_I;
import org.robotcontrol.middleware.idl.WatchDog;
import org.robotcontrol.middleware.rpc.RpcUtils;
import org.robotcontrol.middleware.rpc.RpcValue;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class WatchdogServer implements ServerStub_I {

    private WatchDog watchdog;

    @Override
    public void call(String fnName, RpcValue... args) {
        switch (fnName) {
            case "subscribe":
                watchdog.subscribe(
                        (String) RpcUtils.unwrap(args[0]),
                        (String) RpcUtils.unwrap(args[1]));

                break;

            case "heartbeat":
                watchdog.heartbeat(
                        (String) RpcUtils.unwrap(args[0]));

            default:
                break;
        }
    }
}

