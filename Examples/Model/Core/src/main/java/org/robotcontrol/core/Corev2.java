package org.robotcontrol.core;

import org.robotcontrol.middleware.utils.Environment;
import org.robotcontrol.middleware.utils.Logger;
import org.robotcontrol.middlewarev2.Middleware;
import org.robotcontrol.middlewarev2.idl.Controller;
import org.robotcontrol.middlewarev2.idl.MoveAdapter;
import org.robotcontrol.middlewarev2.idl.StateService;
import org.robotcontrol.middlewarev2.idl.StateService.SelectDirection;
import org.robotcontrol.middlewarev2.rpc.RpcServer;

public class Corev2 {
    private static final Logger logger = new Logger("Corev2");

    public static void main(String[] args) throws InterruptedException {
        StateService stateService = new org.robotcontrol.core.application.stateservice.StateService(new ControllerMock());
        MoveAdapter moveAdapter = new org.robotcontrol.core.application.moveadapter.MoveAdapter(stateService);
        moveAdapter.setSelected("R1");

        RpcServer moveAdapterServer = Middleware.createMoveAdapterServer(
            moveAdapter, 
            Environment.getEnvIntOrExit("MOVEADAPTER_PORT"), 
            "IO"
        );
        moveAdapterServer.start();

        RpcServer stateServiceServer = Middleware.createStateServiceServicer(
            stateService,
            Environment.getEnvIntOrExit("STATESERVICE_PORT"),
            "IO"
        );
        stateServiceServer.start();
        // MoveAdapter client = Middleware.createMoveAdapterClient();

        // Boolean toggle = false;
        // while (true) {
        //     client.setSelected(toggle ? "R1A1": "R1A2");
        //     toggle = !toggle;
        //     Thread.sleep(10);
        // }

        // StateService stateServiceClient = Middleware.createStateServiceClient();
        // Boolean toggle = false;
        // while (true) {
        //     stateServiceClient.select(toggle ? SelectDirection.UP: SelectDirection.DOWN);
        //     toggle = !toggle;
        //     Thread.sleep(1000);
        // }
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

    public static class StateServiceMock implements StateService {
        @Override
        public void reportHealth(String serviceName, String subscription) {
            // TODO Auto-generated method stub
            throw new UnsupportedOperationException("Unimplemented method 'reportHealth'");
        }

        @Override
        public void setError(boolean err, boolean confirm) {
            logger.info("setError(%s, %s)", err, confirm);
        }

        @Override
        public void select(SelectDirection selectDirection) {
            logger.info("select(%s)", selectDirection);
        }
    }

    public static class ControllerMock implements Controller {

        @Override
        public void reportHealth(String serviceName, String subscription) {
            // TODO Auto-generated method stub
            throw new UnsupportedOperationException("Unimplemented method 'reportHealth'");
        }

        @Override
        public void update(byte[] robots, int selected, boolean error, boolean confirm) {
            logger.info("update(%s, %s, %s, %s)", robots, selected, error, confirm);
        }
    }
}
