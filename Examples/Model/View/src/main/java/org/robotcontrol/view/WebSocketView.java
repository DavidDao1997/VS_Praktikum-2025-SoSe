package org.robotcontrol.view;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Arrays;
import java.util.List;

import org.robotcontrol.middleware.RpcUtils;
import org.robotcontrol.middleware.RpcValue;
import org.robotcontrol.middleware.ServerStub;
import org.robotcontrol.middleware.ServerStub_I;
import org.robotcontrol.websocket.RobotWebSocketServer;

public class WebSocketView implements IView,ServerStub_I {

    private final RobotWebSocketServer server;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public WebSocketView(RobotWebSocketServer server) {
        this.server = server;
    }

    @Override
    public void updateView(String[] robots, int selected, boolean error, boolean confirm) {
        System.out.printf(" %s, %s, %s, %s\n",Arrays.toString(robots),selected,error,confirm);
        ViewData data = new ViewData(robots, selected, error, confirm);
        try {
            String json = objectMapper.writeValueAsString(data);
            server.sendUpdate(json);
        } catch (Exception e) {
            e.printStackTrace();
        }
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
    public void call(String fnName, RpcValue... args) {
        List<String> list = (List<String>) RpcUtils.unwrap(args[0]);
        updateView(list.toArray(new String[0]), (int) RpcUtils.unwrap(args[1]), (boolean) RpcUtils.unwrap(args[2]), (boolean) RpcUtils.unwrap(args[3]));
    }

}
