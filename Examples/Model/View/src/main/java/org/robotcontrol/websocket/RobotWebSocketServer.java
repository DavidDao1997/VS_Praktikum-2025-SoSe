package org.robotcontrol.websocket;

import org.java_websocket.server.WebSocketServer;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;

import java.net.InetSocketAddress;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class RobotWebSocketServer extends WebSocketServer {

    private final Set<WebSocket> connections = Collections.synchronizedSet(new HashSet<>());

    public RobotWebSocketServer(int port) {
        super(new InetSocketAddress(port));
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        connections.add(conn);
        System.out.println("Client verbunden: " + conn.getRemoteSocketAddress());
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        connections.remove(conn);
        System.out.println("Client getrennt: " + conn.getRemoteSocketAddress());
    }

    @Override
    public void onMessage(WebSocket conn, String message) {
        System.out.println("Nachricht empfangen: " + message);
        // Optional: Client-Nachrichten verarbeiten
    }

    @Override
    public void onError(WebSocket conn, Exception ex) {
        System.err.println("Fehler: " + ex.getMessage());
    }

    @Override
    public void onStart() {
        System.out.println("WebSocket Server gestartet auf Port " + getPort());
    }

    public void sendUpdate(String json) {
        synchronized (connections) {
            for (WebSocket conn : connections) {
                conn.send(json);
            }
        }
    }
}
