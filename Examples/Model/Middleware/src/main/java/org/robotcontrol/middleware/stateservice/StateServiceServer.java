package org.robotcontrol.middleware.stateservice;

import org.robotcontrol.middleware.ServerStub_I;
import org.robotcontrol.middleware.idl.ActuatorController;
import org.robotcontrol.middleware.idl.StateService;
import org.robotcontrol.middleware.idl.StateService.SelectDirection;
import org.robotcontrol.middleware.rpc.RpcUtils;
import org.robotcontrol.middleware.rpc.RpcValue;
import org.robotcontrol.middleware.utils.Logger;

import lombok.Setter;


@Setter
public class StateServiceServer implements ServerStub_I {
    private final Logger logger = new Logger("StateServiceServer");
    private StateService stateService;
    
    public StateServiceServer(StateService stateService) {
        this.stateService = stateService;
    }

    @Override
    public void call(String fnName, RpcValue... args) {
        switch (fnName) {
            case "setError":
                stateService.setError(
                        (boolean) RpcUtils.unwrap(args[0]),
                        (boolean) RpcUtils.unwrap(args[1]));
                break;
            case "reportHealth":
                stateService.reportHealth(
                        (String) RpcUtils.unwrap(args[0]),
                        (String) RpcUtils.unwrap(args[1]));
            case "select":
                stateService.select(
                    SelectDirection.values()[(int) RpcUtils.unwrap(args[0])]
                );
                break;
            default:
                logger.info("Unimplemented method called: %s",fnName);
                break;
        }
    }
}
