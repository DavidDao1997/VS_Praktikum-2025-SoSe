package org.robotcontrol.core;

import org.robotcontrol.core.application.stateservice.StateService;
import org.robotcontrol.middleware.utils.Environment;
import org.robotcontrol.middleware.utils.Logger;
import org.robotcontrol.middlewarev2.Middleware;
import org.robotcontrol.middlewarev2.idl.Controller;
import org.robotcontrol.middlewarev2.idl.MoveAdapter;
import org.robotcontrol.middlewarev2.rpc.RpcServer;

public class Corev2 {
    private static final Logger logger = new Logger("Corev2");

    public static void main(String[] args) throws InterruptedException {
        Controller controller = new org.robotcontrol.core.application.controller.rpc.Controller();
        StateService stateService = new org.robotcontrol.core.application.stateservice.StateService(controller);
        MoveAdapter moveAdapter = new org.robotcontrol.core.application.moveadapter.MoveAdapter(stateService);
        stateService.setMoveAdapter(moveAdapter);

        RpcServer moveAdapterServer = Middleware.createMoveAdapterServer(
            moveAdapter, 
            Environment.getEnvIntOrExit("MOVEADAPTER_PORT"), 
            "IO"
        );
        moveAdapterServer.start();

        RpcServer stateServiceServer = Middleware.createStateServiceServicer(
            stateService,
            Environment.getEnvIntOrExit("STATESERVICE_PORT"),
            "IO", "watchdog"
        );
        stateServiceServer.start();
    }

    public static class MoveAdapterMock implements MoveAdapter {

        @Override
        public void move(RobotDirection robotDirection) {
            logger.info("move(%s)", robotDirection);
        }

        @Override
        public void setSelected(String selected) {
            logger.info("setSelected(%s)", selected);
        }
    }
}
