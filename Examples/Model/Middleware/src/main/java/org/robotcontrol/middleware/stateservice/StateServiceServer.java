package org.robotcontrol.middleware.stateservice;

import org.robotcontrol.middleware.ServerStub_I;
import org.robotcontrol.middleware.idl.ActuatorController;
import org.robotcontrol.middleware.idl.StateService;
import org.robotcontrol.middleware.rpc.RpcUtils;
import org.robotcontrol.middleware.rpc.RpcValue;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class StateServiceServer implements ServerStub_I {
    private StateService stateService;

    @Override
    public void call(String fnName, RpcValue... args) {
                switch (fnName) {
            case "setError":
                stateService.setError(
                    (boolean) RpcUtils.unwrap(args[0]), 
                    (boolean) RpcUtils.unwrap(args[1])
                );
                break;
            case "reportHealth":
                stateService.reportHealth(
                    (String) RpcUtils.unwrap(args[0])
                );
            default:
                System.out.printf("%s.call(fnName: %s, ...): Unimplemented method called", getClass().getSimpleName(), fnName);
                break;
        }
    }
}
