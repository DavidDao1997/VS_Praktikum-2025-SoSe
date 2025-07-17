package org.robotcontrol.middleware;

import java.util.Arrays;

import org.robotcontrol.middleware.idl.ActuatorController;
import org.robotcontrol.middleware.idl.MoveAdapter;
import org.robotcontrol.middleware.idl.StateService;
import org.robotcontrol.middleware.idl.UI;
import org.robotcontrol.middleware.idl.Watchdog;
import org.robotcontrol.middleware.internal.idl.ActuatorControllerImpl;
import org.robotcontrol.middleware.internal.idl.MoveAdapterImpl;
import org.robotcontrol.middleware.internal.idl.StateServiceImpl;
import org.robotcontrol.middleware.internal.idl.UIImpl;
import org.robotcontrol.middleware.internal.idl.WatchdogImpl;
import org.robotcontrol.middleware.rpc.Callable;
import org.robotcontrol.middleware.rpc.Invokable;
import org.robotcontrol.middleware.rpc.RpcServer;
import org.robotcontrol.middleware.utils.Environment;

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
            "select", "reportHealth"
        );
    }

 

    public static ActuatorController createActuatorControllerClient(String serviceName) {
        return new ActuatorControllerImpl.Client(serviceName);
    }
    public static RpcServer createActuatorControllerServer(ActuatorController actuatorController, Integer port, String serviceName, String... clients) {
        return new ActuatorControllerImpl.Server(port, actuatorController, clients, serviceName, "move");
    }

    public static UI createUIClient(){
        return new UIImpl.Client("ui");
    }

    public static RpcServer createUIServer(UI ui, Integer port, String... clients){
      return  new UIImpl.Server(port, ui, clients, "ui", "updateView");
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
