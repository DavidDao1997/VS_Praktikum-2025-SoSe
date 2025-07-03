package org.robotcontrol.http;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.robotcontrol.middleware.utils.Logger;

public class SimpleHttpServer {
    private final Logger logger = new Logger("SimpleHttpServer");

    public static void startServer(int port, String filePath) throws IOException {
        Logger logger = new Logger("SimpleHttpServer");
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
        server.createContext("/", new FileHandler(filePath));
        server.setExecutor(null);
        server.start();
        logger.debug("HTTP Server gestartet auf Port {}", port);
    }

    private static class FileHandler implements HttpHandler {
        private final String filePath;

        public FileHandler(String filePath) {
            this.filePath = filePath;
        }

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            byte[] response = Files.readAllBytes(Paths.get(filePath));
            exchange.getResponseHeaders().set("Content-Type", "text/html");
            exchange.sendResponseHeaders(200, response.length);
            OutputStream os = exchange.getResponseBody();
            os.write(response);
            os.close();
        }
    }
}
