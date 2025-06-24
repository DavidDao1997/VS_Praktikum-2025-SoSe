package org.robotcontrol.core;

import io.grpc.BindableService;
import org.robotcontrol.middleware.Server;
import org.robotcontrol.middleware.services.MoveAdapterServer;
import org.robotcontrol.middleware.services.StateServiceServer;
import org.robotcontrol.core.application.controller.rpc.ControllerMock;
import org.robotcontrol.core.application.moveadapter.MoveAdapter;
import org.robotcontrol.core.application.stateservice.StateService;

/**
 * Core is a central service that consolidates the functionality of 
 * {@code Controller}, {@code StateService}, and {@code MoveAdapter}.
 * <p>
 * This unified service provides a single entry point for coordinating
 * operations that span across these previously independent components.
 */
public class Core {
    public static void main(String[] args) {
        StateService stateService = new StateService(new ControllerMock());
        stateService.register("R1A1");
        stateService.register("R1A2");
        stateService.register("R1A3");
        stateService.register("R1A4");

        MoveAdapter moveAdapter = new MoveAdapter(stateService);


        Server server = new Server(
            50052,
                (BindableService) new StateServiceServer(stateService),
                (BindableService) new MoveAdapterServer(moveAdapter)
        );

        server.Listen();
        server.awaitTermination();
    }
}
