package org.robotcontrol.view;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;


import org.robotcontrol.websocket.RobotWebSocketServer;

public class WebSocketView implements org.robotcontrol.middleware.idl.UI {

    private final RobotWebSocketServer server;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public WebSocketView(RobotWebSocketServer server) {
        this.server = server;
    }

    private static class ViewData {
        public String[] available_robots;
        public int selected_robot_idx;
        public boolean error;
        public boolean confirmation;

        public ViewData(String[] robots, int selected, boolean error, boolean confirm) {
            this.available_robots = robots;
            this.selected_robot_idx = selected;
            this.error = error;
            this.confirmation = confirm;
        }
    }
    // Diese Methode wird verwendet, um Null-Bytes aus den robotBitmap-Daten zu entfernen
    private String[] sanitizeRobots(String robotsString) {
        // Entfernen von Null-Bytes, die durch UTF-8-Konvertierung entstehen könnten
        robotsString = robotsString.replaceAll("\u0000", "");
        return robotsString.split(",");  // Angenommen, Roboter sind durch Kommas getrennt
    }


    @Override
    public void updateView(String[] robots, int selected, boolean error, boolean confirm) {
        // Umwandlung des byte[] robots in String
        
        System.out.println("Received robots string: " + robots);
        




        System.out.printf("Selected: %d, Error: %b, Confirmation: %b\n", selected, error, confirm);

        // Erstellen der ViewData und JSON
        ViewData data = new ViewData(robots, selected, error, confirm);
        try {
            String json = objectMapper.writeValueAsString(data);
            System.out.println("Sending JSON: " + json);  // Zum Debuggen
            server.sendUpdate(json);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
