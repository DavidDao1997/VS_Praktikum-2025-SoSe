package org.robotcontrol.core;

import org.robotcontrol.middlewarev2.Middleware;
import org.robotcontrol.middlewarev2.idl.MoveAdapter;
import org.robotcontrol.middlewarev2.idl.MoveAdapter.RobotDirection;

public class Corev2MockClient {
        public static void main(String[] args) throws InterruptedException {
            MoveAdapter moveAdapter = Middleware.createMoveAdapterClient();

            while (true) {
                moveAdapter.move(RobotDirection.BACKWARD);
                Thread.sleep(3000);
            }
        }

}
