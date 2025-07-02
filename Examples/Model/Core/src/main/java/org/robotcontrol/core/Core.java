package org.robotcontrol.core;

import org.robotcontrol.middleware.idl.View;
import org.robotcontrol.middleware.moveadapter.MoveAdapterServer;
import org.robotcontrol.middleware.rpc.RpcServer;
import org.robotcontrol.middleware.stateservice.StateServiceServer;
import org.robotcontrol.middleware.utils.Mocker;
//import org.robotcontrol.view.ViewMock;
import org.robotcontrol.view.ViewClient;
import org.robotcontrol.core.application.controller.rpc.Controller;
import org.robotcontrol.core.application.moveadapter.MoveAdapter;
import org.robotcontrol.core.application.stateservice.StateService;
import org.robotcontrol.core.application.stateservice.StateService.SelectDirection;

/**
 * Core is a central service that consolidates the functionality of 
 * {@code Controller}, {@code StateService}, and {@code MoveAdapter}.
 * <p>
 * This unified service provides a single entry point for coordinating
 * operations that span across these previously independent components.
 */
public class Core {
    public static void main(String[] args) {
        // FIXME use client
        StateService stateService = new StateService(new Controller(Mocker.mock(View.class)));
        stateService.register("R1A1");
        stateService.register("R1A2");
        stateService.register("R1A3");
        stateService.register("R1A4");
        stateService.setError(false, true);

        stateService.register("R2A1");
        stateService.register("R2A2");
        stateService.register("R2A3");
        stateService.register("R2A4");
        stateService.select(SelectDirection.UP);

        stateService.register("R3A1");
        stateService.register("R3A2");
        stateService.register("R3A3");
        stateService.register("R3A4");
        stateService.select(SelectDirection.UP);

        MoveAdapter moveAdapter = new MoveAdapter(stateService);

        RpcServer server = new RpcServer();
        server.addService(new MoveAdapterServer(moveAdapter), "moveAdapter", "move");
        server.addService(new StateServiceServer(stateService), "StateService", "select", "reportHealth");

        server.Listen();
        server.awaitTermination();
    }
}
