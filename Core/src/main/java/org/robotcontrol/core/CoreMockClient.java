package org.robotcontrol.core;

import org.robotcontrol.middleware.Middleware;
import org.robotcontrol.middleware.idl.MoveAdapter;
import org.robotcontrol.middleware.idl.MoveAdapter.RobotDirection;

public class CoreMockClient {
        public static void main(String[] args) throws InterruptedException {
            MoveAdapter moveAdapter = Middleware.createMoveAdapterClient();

            while (true) {
                moveAdapter.move(RobotDirection.BACKWARD);
                Thread.sleep(3000);
            }
        }

}
