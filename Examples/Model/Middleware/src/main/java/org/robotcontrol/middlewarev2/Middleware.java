package org.robotcontrol.middlewarev2;

import java.util.Arrays;

import org.robotcontrol.middleware.utils.Environment;
import org.robotcontrol.middlewarev2.idl.ActuatorController;
import org.robotcontrol.middlewarev2.idl.MoveAdapter;
import org.robotcontrol.middlewarev2.idl.StateService;
import org.robotcontrol.middlewarev2.idl.Watchdog;
import org.robotcontrol.middlewarev2.internal.idl.ActuatorControllerImpl;
import org.robotcontrol.middlewarev2.internal.idl.MoveAdapterImpl;
import org.robotcontrol.middlewarev2.internal.idl.StateServiceImpl;
import org.robotcontrol.middlewarev2.internal.idl.WatchdogImpl;
import org.robotcontrol.middlewarev2.rpc.Callable;
import org.robotcontrol.middlewarev2.rpc.Invokable;
import org.robotcontrol.middlewarev2.rpc.RpcServer;

public final class Middleware {
   
    // each idl Interface has these 2 methods
    public static MoveAdapter createMoveAdapterClient() {
        return new MoveAdapterImpl.Client();
    }
    public static RpcServer createMoveAdapterServer(MoveAdapter moveAdapter, Integer port, String... clients) {
        return new MoveAdapterImpl.Server(
            port, 
            moveAdapter, 
            clients, 
            "MoveAdapter", 
            "move"
        );
    }

    public static StateService createStateServiceClient() {
        return new StateServiceImpl.Client();
    }
    public static RpcServer createStateServiceServicer(StateService stateService, Integer port, String... clients) {
        return new StateServiceImpl.Server(
            port, 
            stateService, 
            clients, 
            "StateService", 
            "select"
        );
    }

    public static ActuatorController createActuatorControllerClient(String serviceName) {
        return new ActuatorControllerImpl.Client(serviceName);
    }
    public static RpcServer createActuatorControllerServer(ActuatorController actuatorController, Integer port, String serviceName, String... clients) {
        return new ActuatorControllerImpl.Server(port, actuatorController, clients, serviceName, "move");
    }

    // each idl Interface has these 2 methods
    // public static StateService createStateServiceClient() {}
    // public static RpcServer createStateServiceServer(StateService stateServiceImpl) {}

    public static Watchdog createWatchdogClient() {
        return new WatchdogImpl.Client();
    }
    // WatchdogServer: exposed via middleware.v2.server.WatchdogServer
    
    // DnsClient: only used internally
    // DnsServer: exposed in middleware.v2.server.DnsServer
    
    // it could be considered to allow users to implement their own custom client server pairs
    public static Invokable createDynamicClient(String serviceName, String... fnNames) {
        return null;
    }
    public static RpcServer createDynamicServer(Callable Callable, String serviceName, String... fnNames) {
        return null;
    }
}
