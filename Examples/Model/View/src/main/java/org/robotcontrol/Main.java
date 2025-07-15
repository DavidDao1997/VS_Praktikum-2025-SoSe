package org.robotcontrol;

import org.robotcontrol.http.SimpleHttpServer;
import org.robotcontrol.middleware.rpc.RpcServer;
import org.robotcontrol.middleware.ui.UIServer;
import org.robotcontrol.middleware.utils.Environment;
import org.robotcontrol.middleware.utils.Logger;
import org.robotcontrol.view.WebSocketView;
import org.robotcontrol.websocket.RobotWebSocketServer;

public class Main {
    private static final Logger logger = new Logger("DnsService");

    public static void main(String[] args) throws Exception {
        Integer PORT = Environment.getEnvIntOrExit("PORT");
        
        // WebSocket-Server starten
        RobotWebSocketServer wsServer = new RobotWebSocketServer(4567);
        wsServer.start();

        // HTTP-Server in einem eigenen Thread starten
        SimpleHttpServer.startServer(8080, "ui.html");
       
        // WebSocket View erstellen
        WebSocketView view = new WebSocketView(wsServer);

        // UDP-Server starten (z.B. Port 5000)
        // Thread udpThread = new Thread(new UdpViewServer(view, 5000));
        // udpThread.start();
        RpcServer server = new RpcServer();
        server.addService(PORT, new UIServer(view), "UI","updateView");
        server.Listen();
        System.out.println("System bereit.");
        server.awaitTermination();
        System.out.println("System Terminiert.");

    }
}
