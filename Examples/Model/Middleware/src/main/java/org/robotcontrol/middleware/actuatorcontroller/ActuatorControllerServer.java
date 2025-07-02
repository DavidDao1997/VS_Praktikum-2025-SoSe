package org.robotcontrol.middleware.actuatorcontroller;

import org.robotcontrol.middleware.ServerStub_I;
import org.robotcontrol.middleware.idl.ActuatorController;
import org.robotcontrol.middleware.rpc.RpcUtils;
import org.robotcontrol.middleware.rpc.RpcValue;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ActuatorControllerServer implements ServerStub_I {
    private ActuatorController actuatorController;

    @Override
    public void call(String fnName, RpcValue... args) {
        switch (fnName) {
            case "move":
                actuatorController.move(
                    (ActuatorController.Direction) RpcUtils.unwrap(args[0])
                );
                break;
            default:
                System.out.printf("%s.call(fnName: %s, ...): Unimplemented method called", getClass().getSimpleName(), fnName);
                break;
        }
    }
}
