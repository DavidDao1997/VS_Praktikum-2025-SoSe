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
        logger.debug("Client verbunden: %s",conn.getRemoteSocketAddress());

        // Send initial state if available
        if (latestStateJson != null) {
            conn.send(latestStateJson);
        }
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        connections.remove(conn);
        logger.debug("Client getrennt: %s", conn.getRemoteSocketAddress());
    }

    @Override
    public void onMessage(WebSocket conn, String message) {
        logger.debug("Nachricht empfangen: %s", message);
        // Optional: Client-Nachrichten verarbeiten
    }

    @Override
    public void onError(WebSocket conn, Exception ex) {
        logger.error("Fehler: %s", ex.getMessage());
    }

    @Override
    public void onStart() {
        logger.info("WebSocket Server gestartet auf Port %s", getPort());
    }

    public void sendUpdate(String json) {
        latestStateJson = json;  // Update latest known state

        synchronized (connections) {
            for (WebSocket conn : connections) {
                conn.send(json);
            }
        }
    }
}
