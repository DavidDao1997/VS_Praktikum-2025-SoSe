package org.robotcontrol.middleware.registeractuator;

import java.util.HashMap;
import java.util.Map;

import org.robotcontrol.middleware.ServerStub_I;
import org.robotcontrol.middleware.idl.RegisterActuator;
import org.robotcontrol.middleware.rpc.RpcUtils;
import org.robotcontrol.middleware.rpc.RpcValue;
import org.robotcontrol.middleware.watchdog.WatchdogClient;

public class RegisterActuatorServer implements ServerStub_I {
    private RegisterActuator registerActuator;
    private WatchdogClient wdClient;
    private final Map<String, Boolean> actuatorStates = new HashMap<>();

    public RegisterActuatorServer(RegisterActuator registerActuator) {
        this.registerActuator = registerActuator;
        wdClient = new WatchdogClient();
        wdClient.subscribe("registerActuator", "R*");
    }


    @Override
    public void call(String fnName, RpcValue... args) {
        switch (fnName) {
            case "reportHealth":
                String actuatorName = (String) RpcUtils.unwrap(args[0]);
                boolean isAlive = (boolean) RpcUtils.unwrap(args[1]);

                Boolean previousState = actuatorStates.get(actuatorName);
                if (previousState == null || previousState != isAlive) {
                    actuatorStates.put(actuatorName, isAlive);
                    registerActuator.registerActuator(actuatorName, isAlive);
                }
                break;

            default:
                System.out.printf("%s.call(fnName: %s, ...): Unimplemented method called%n", 
                                  getClass().getSimpleName(), fnName);
                break;
        }
    }
    
}
