package org.robotcontrol.core.application.controller.rpc;

import java.util.Arrays;
import java.util.stream.Collectors;

import org.robotcontrol.middleware.Middleware;
import org.robotcontrol.middleware.idl.UI;
import org.robotcontrol.middleware.utils.Logger;

public class Controller implements org.robotcontrol.middleware.idl.Controller {
    private static final Logger logger = new Logger("Controller");

    private final UI ui;

    public Controller() {
        this.ui = Middleware.createUIClient();
    }

    @Override
    public void update(String[] robots, int selected, boolean error, boolean confirm) {
        logger.info("update(%s, %s, %s, %s)", 
            Arrays.stream(robots)
                .map(r -> String.format(" %s", r))
                .collect(Collectors.joining()), 
            selected, 
            error, 
            confirm
        );
        ui.updateView(robots, selected, error, confirm);
    }
}
