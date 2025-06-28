package org.robotcontrol.core;

import org.robotcontrol.middleware.udp.MoveAdapterServer;
import org.robotcontrol.middleware.udp.StateServiceServer;
import org.robotcontrol.middleware.udp.UdpServer;
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
        // FIXME use client
        StateService stateService = new StateService(new ControllerMock());
        stateService.register("R1A1");
        stateService.register("R1A2");
        stateService.register("R1A3");
        stateService.register("R1A4");

        MoveAdapter moveAdapter = new MoveAdapter(stateService);

        UdpServer server = new UdpServer();
        server.addService(45054, new MoveAdapterServer(moveAdapter));
        server.addService(45055, new StateServiceServer(stateService));

        server.Listen();
        server.awaitTermination();
    }
}
