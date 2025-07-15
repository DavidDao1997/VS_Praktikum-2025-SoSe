package org.robotcontrol.core;

import java.io.IOException;

// import org.robotcontrol.core.application.controller.rpc.Controller;
// import org.robotcontrol.core.application.moveadapter.MoveAdapter;
// import org.robotcontrol.core.application.stateservice.StateService;
// import org.robotcontrol.middleware_old.moveadapter.MoveAdapterServer;
// import org.robotcontrol.middleware_old.registeractuator.RegisterActuatorServer;
// import org.robotcontrol.middleware_old.stateservice.StateServiceServer;
// import org.robotcontrol.middleware_old.ui.UIClient;


/**
 * Core is a central service that consolidates the functionality of 
 * {@code Controller}, {@code StateService}, and {@code MoveAdapter}.
 * <p>
 * This unified service provides a single entry point for coordinating
 * operations that span across these previously independent components.
 */
public class Core {
    public static void main(String[] args) throws IOException {
        // Integer PORT_MOVEADAPTER = Environment.getEnvIntOrExit("PORT_MOVEADAPTER");
        // Integer PORT_STATESERVICE = Environment.getEnvIntOrExit("PORT_STATESERVICE");
        // Integer PORT_REGISTERACTUATOR = Environment.getEnvIntOrExit("PORT_REGISTERACTUATOR");



        // StateService stateService = new StateService(new Controller(new UIClient()));
        // MoveAdapter moveAdapter = new MoveAdapter(stateService);
        // stateService.setMoveAdapter(moveAdapter);
        // // stateService.registerActuator("R1A1", true);
        // stateService.registerActuator("R1A2", true);
        // stateService.registerActuator("R1A3", true);
        // stateService.registerActuator("R1A4", true);
        // // stateService.setError(false, true);

        // // stateService.registerActuator("R2A1", true);
        // // stateService.registerActuator("R2A2", true);
        // // stateService.registerActuator("R2A3", true);
        // // stateService.registerActuator("R2A4", true);
        // stateService.select(SelectDirection.UP);

        // // stateService.registerActuator("R3A1", true);
        // // stateService.registerActuator("R3A2", true);
        // // stateService.registerActuator("R3A3", true);
        // // stateService.registerActuator("R3A4", true);
        // // stateService.select(SelectDirection.UP);

        // RpcServer server = new RpcServer();
        // server.addService(PORT_MOVEADAPTER, new MoveAdapterServer(moveAdapter), "moveAdapter", "move");
        // server.addService(PORT_STATESERVICE, new StateServiceServer(stateService), "stateService", "select");
        // server.addService(PORT_REGISTERACTUATOR, new RegisterActuatorServer(stateService), "registerActuator", "reportHealth");

        // server.Listen();
        // server.awaitTermination();
    }
}
