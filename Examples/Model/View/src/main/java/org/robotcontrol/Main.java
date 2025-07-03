package org.robotcontrol;

import org.robotcontrol.http.SimpleHttpServer;
import org.robotcontrol.middleware.rpc.RpcServer;
import org.robotcontrol.middleware.ui.UIServer;
import org.robotcontrol.view.WebSocketView;
import org.robotcontrol.websocket.RobotWebSocketServer;

public class Main {

    public static void main(String[] args) throws Exception {
        // WebSocket-Server starten
        RobotWebSocketServer wsServer = new RobotWebSocketServer(4568);
        wsServer.start();

        // HTTP-Server starten
        SimpleHttpServer.startServer(8080, "ui.html");

        // WebSocket View erstellen
        WebSocketView view = new WebSocketView(wsServer);

        // UDP-Server starten (z.B. Port 5000)
        // Thread udpThread = new Thread(new UdpViewServer(view, 5000));
        // udpThread.start();
        RpcServer server = new RpcServer();
        server.addService(new UIServer(view), "UI","updateView");
        server.Listen();
        System.out.println("System bereit.");
        server.awaitTermination();
        System.out.println("System Terminiert.");

    }
}
