package org.robotcontrol;

import org.robotcontrol.http.SimpleHttpServer;
import org.robotcontrol.middleware.udp.UdpServer;
import org.robotcontrol.udp.UdpViewServer;
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
        UdpServer server = new UdpServer();
        server.addService(5000, view);
        server.Listen();
        System.out.println("System bereit.");
        server.awaitTermination();
        System.out.println("System Terminiert.");

    }
}
