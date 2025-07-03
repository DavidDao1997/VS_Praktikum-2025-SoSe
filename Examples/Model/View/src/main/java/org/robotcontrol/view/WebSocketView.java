package org.robotcontrol.view;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

import org.robotcontrol.middleware.ServerStub_I;
import org.robotcontrol.middleware.idl.UI;
import org.robotcontrol.middleware.rpc.RpcUtils;
import org.robotcontrol.middleware.rpc.RpcValue;
import org.robotcontrol.websocket.RobotWebSocketServer;

public class WebSocketView implements UI {

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

    @Override
    public void updateView(byte[] robotBitmap, int selected, boolean error, boolean confirm) {
        // Umwandlung des byte[] robots in String[] (innerhalb der call-Methode)
        String robotsString = new String(robotBitmap, StandardCharsets.UTF_8);

        // Annahme: Die Roboter sind durch Kommas getrennt (falls das Format es
        // erfordert)
        String[] robotsArray = robotsString.split(",");

        System.out.printf(" %s, %s, %s, %s\n", robotsArray, selected, error, confirm);
        ViewData data = new ViewData(robotsArray, selected, error, confirm);
        try {
            String json = objectMapper.writeValueAsString(data);
            System.out.printf(json);
            server.sendUpdate(json);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
