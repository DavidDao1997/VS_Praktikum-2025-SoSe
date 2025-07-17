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

    public static byte[] toByteArray(InputStream is) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        int nRead;
        byte[] data = new byte[4096];
        while ((nRead = is.read(data, 0, data.length)) != -1) {
            buffer.write(data, 0, nRead);
        }
        return buffer.toByteArray();
    }

    public static void startServer(int port, String filePath) throws IOException {
        Logger logger = new Logger("SimpleHttpServer");
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
        server.createContext("/", exchange -> {
            InputStream is = SimpleHttpServer.class.getResourceAsStream("/ui.html");
            if (is == null) {
                exchange.sendResponseHeaders(404, -1);
                return;
            }

            byte[] bytes;
            try {
                bytes = toByteArray(is);
            } catch (IOException e) {
                exchange.sendResponseHeaders(500, -1);
                return;
            }

            exchange.sendResponseHeaders(200, bytes.length);
            OutputStream os = exchange.getResponseBody();
            os.write(bytes);
            os.close();
        });
        server.setExecutor(null);
        server.start();
        logger.debug("HTTP Server gestartet auf Port %s", port);
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
