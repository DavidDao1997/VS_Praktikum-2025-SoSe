package org.robotcontrol;

import org.robotcontrol.http.SimpleHttpServer;

import org.robotcontrol.middleware.utils.Environment;
import org.robotcontrol.middleware.utils.Logger;
import org.robotcontrol.middlewarev2.Middleware;
import org.robotcontrol.middlewarev2.idl.UI;
import org.robotcontrol.middlewarev2.rpc.RpcServer;
import org.robotcontrol.view.WebSocketView;
import org.robotcontrol.websocket.RobotWebSocketServer;

public class Main {
    //private static final Logger logger = new Logger("DnsService");

    public static void main(String[] args) throws Exception {
        Integer PORT = Environment.getEnvIntOrExit("PORT");
        
        // WebSocket-Server starten
        RobotWebSocketServer wsServer = new RobotWebSocketServer(4567);
        wsServer.start();

        // HTTP-Server in einem eigenen Thread starten
        SimpleHttpServer.startServer(8080, "ui.html");
       
        // WebSocket View erstellen
        UI ui = new WebSocketView(wsServer);

       

        RpcServer uiServer = Middleware.createUIServer(
            ui,
            Environment.getEnvIntOrExit("PORT"),
            "core"
        );
        uiServer.start();

    }
}
