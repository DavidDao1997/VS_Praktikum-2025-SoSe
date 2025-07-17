package org.robotcontrol.websocket;

import org.java_websocket.server.WebSocketServer;
import org.robotcontrol.middleware.utils.Logger;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;

import java.net.InetSocketAddress;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class RobotWebSocketServer extends WebSocketServer {
    private final Logger logger = new Logger("RobotWebSocketServer");
    private final Set<WebSocket> connections = Collections.synchronizedSet(new HashSet<>());
    private volatile String latestStateJson = null;  // Shared cached state


    public RobotWebSocketServer(int port) {
        super(new InetSocketAddress(port));
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        connections.add(conn);
        logger.debug("Client verbunden: %s", conn.getRemoteSocketAddress());
        
        // Sende initialen Zustand, falls vorhanden
        if (latestStateJson != null) {
            conn.send(latestStateJson);
            logger.debug("Initialer Zustand gesendet: %s", latestStateJson);
        }
    }


    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        connections.remove(conn);
        logger.debug("Client getrennt: %s (code: %d, reason: %s, remote: %b)", conn.getRemoteSocketAddress(), code, reason, remote);
    }


    @Override
    public void onMessage(WebSocket conn, String message) {
        logger.debug("Nachricht empfangen: %s", message);
        // Optional: Client-Nachrichten verarbeiten
    }

    @Override
    public void onError(WebSocket conn, Exception ex) {
        logger.error("Fehler: %s", ex.getMessage());
        ex.printStackTrace();  // Detaillierte Ausgabe des Stacktraces
    }

    @Override
    public void onStart() {
        logger.info("WebSocket Server gestartet auf Port %s", getPort());
        if (getPort() == -1) {
            logger.error("WebSocket Server konnte nicht auf dem angegebenen Port starten.");
        }
    }


    public void sendUpdate(String json) {
        if (json == null || json.isEmpty()) {
            logger.warn("Leere oder null JSON-Nachricht wird nicht gesendet.");
            return;
        }
        latestStateJson = json;  // Bereinige die JSON-Nachricht
        System.out.println("Sanitized JSON: " + latestStateJson);
        System.out.print(connections);
        synchronized (connections) {
            for (WebSocket conn : connections) {
                System.out.print(conn);
                conn.send(latestStateJson);
            }
        }
    }


}
