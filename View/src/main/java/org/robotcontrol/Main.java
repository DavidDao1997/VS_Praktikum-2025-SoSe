package org.robotcontrol;

import org.robotcontrol.http.SimpleHttpServer;
import org.robotcontrol.middleware.Middleware;
import org.robotcontrol.middleware.idl.UI;
import org.robotcontrol.middleware.rpc.RpcServer;
import org.robotcontrol.middleware.utils.Environment;
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

        Runtime.getRuntime().addShutdownHook(
            new Thread(() -> {
                try {
                    wsServer.stop();
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            })
        );
    }
}
