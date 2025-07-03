package org.robotcontrol.core;

import org.robotcontrol.middleware.idl.View;
import org.robotcontrol.middleware.moveadapter.MoveAdapterServer;
import org.robotcontrol.middleware.registeractuator.RegisterActuatorServer;
import org.robotcontrol.middleware.rpc.RpcServer;
import org.robotcontrol.middleware.stateservice.StateServiceServer;
import org.robotcontrol.middleware.utils.Mocker;
import org.robotcontrol.view.ViewClient;
import org.robotcontrol.view.WebSocketView;
import org.robotcontrol.websocket.RobotWebSocketServer;

import java.io.IOException;

import org.robotcontrol.core.application.controller.rpc.Controller;
import org.robotcontrol.core.application.moveadapter.MoveAdapter;
import org.robotcontrol.core.application.stateservice.StateService;
import org.robotcontrol.core.application.stateservice.StateService.SelectDirection;
import org.robotcontrol.http.SimpleHttpServer;

/**
 * Core is a central service that consolidates the functionality of 
 * {@code Controller}, {@code StateService}, and {@code MoveAdapter}.
 * <p>
 * This unified service provides a single entry point for coordinating
 * operations that span across these previously independent components.
 */
public class Core {
    public static void main(String[] args) throws IOException {
        // WebSocket-Server starten
        RobotWebSocketServer wsServer = new RobotWebSocketServer(4571);
        wsServer.start();

        // HTTP-Server starten
        SimpleHttpServer.startServer(8083, "ui.html");

        // WebSocket View erstellen
        WebSocketView view = new WebSocketView(wsServer);
        StateService stateService = new StateService(new Controller(view));
        // stateService.registerActuator("R1A1", true);
        stateService.registerActuator("R1A2", true);
        stateService.registerActuator("R1A3", true);
        stateService.registerActuator("R1A4", true);
        // stateService.setError(false, true);

        // stateService.registerActuator("R2A1", true);
        // stateService.registerActuator("R2A2", true);
        // stateService.registerActuator("R2A3", true);
        // stateService.registerActuator("R2A4", true);
        // stateService.select(SelectDirection.UP);

        // stateService.registerActuator("R3A1", true);
        // stateService.registerActuator("R3A2", true);
        // stateService.registerActuator("R3A3", true);
        // stateService.registerActuator("R3A4", true);
        // stateService.select(SelectDirection.UP);

        MoveAdapter moveAdapter = new MoveAdapter(stateService);

        RpcServer server = new RpcServer();
        server.addService(new MoveAdapterServer(moveAdapter), "moveAdapter", "move");
        server.addService(new StateServiceServer(stateService), "stateService", "select");
        server.addService(new RegisterActuatorServer(stateService), "registerActuator", "reportHealth");

        server.Listen();
        server.awaitTermination();
    }
}
