package org.robotcontrol.core.application.moveadapter;

import org.robotcontrol.middleware.ServerStub;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.robotcontrol.core.application.actuatorcontroller.rpc.ActuatorController;
import org.robotcontrol.core.application.actuatorcontroller.rpc.ActuatorController.ActuatorDirection;
import org.robotcontrol.core.application.actuatorcontroller.rpc.ActuatorControllerMock;
import org.robotcontrol.core.application.stateservice.StateService_I;

import lombok.AllArgsConstructor;


@AllArgsConstructor
public class MoveAdapter extends ServerStub {

	 private final ScheduledExecutorService hbExec = Executors.newSingleThreadScheduledExecutor();
     private final WatchDogClient           wd = new WatchDogClient(
                                                    "127.0.0.1", 45060,      // watchdog host/port
                                                    "MoveAdapter",           // self-name
                                                    null);                   // no auto‐HB (we schedule manually)

    {   // instance-initializer block → runs once after ctor
        // 1. heartbeat every second
        hbExec.scheduleAtFixedRate(() -> wd.checkIn(), 0, 1, TimeUnit.SECONDS);

        // 2. ask watchdog to call back on our main stub port (45070 here)
        wd.subscribe("Controller", "127.0.0.1", 45070);
    }

    /** Called by MoveAdapterServer when watchdog invokes reportHealthy(...) */
    public void onHealthUpdate(List<String> healthy) {
        boolean controllerUp = healthy.contains("Controller");
        if (!controllerUp) {
            // degrade gracefully, flag error in UI, etc.
            stateService.setError(true, false);
            System.err.println("[MoveAdapter] Controller is DOWN!");
        } else {
            System.out.println("[MoveAdapter] Controller is OK");
        }
    }
	private StateService_I stateService;

	public enum RobotDirection{
		LEFT,
		RIGHT,
		UP,
		DOWN,
		BACKWARD,
		FORWARD,
		OPEN,
		CLOSE
	}
	
    public void move(int md) {
		RobotDirection[] values = RobotDirection.values();
		if (md < 0 || md >= values.length) {
			throw new IllegalArgumentException("Invalid RobotDirection index: " + md);
		}
		move(values[md]);
	}
	
	public void move(RobotDirection rd) {
		String selectedRobot = stateService.getSelected();
		
		
		if (selectedRobot == null) {
			stateService.setError(true, false);
			return;
		}
		
		Integer actuatorId;
		ActuatorDirection direction;

		switch (rd) {
			case LEFT:
				actuatorId = 1;
				direction = ActuatorDirection.DECREASE;
				break;
			case DOWN:
				actuatorId = 2;
				direction = ActuatorDirection.DECREASE;
				break;
			case BACKWARD:
				actuatorId = 3;
				direction = ActuatorDirection.DECREASE;
				break;
			case CLOSE:		
				actuatorId = 4;
				direction = ActuatorDirection.DECREASE;
				break;
			case RIGHT:
				actuatorId = 1;
				direction = ActuatorDirection.INCREASE;
				break;
			case UP:
				actuatorId = 2;
				direction = ActuatorDirection.INCREASE;
				break;
			case FORWARD:
				actuatorId = 3;
				direction = ActuatorDirection.INCREASE;
				break;
			case OPEN:
				actuatorId = 4;
				direction = ActuatorDirection.INCREASE;
				break;
			default:
				System.out.println("ILLEGAL MOVEMENT");
				stateService.setError(true, false);
				return;
		}

		// FIXME use client
		ActuatorControllerMock acm = new ActuatorControllerMock("R1A1");
		acm.move(direction);

		stateService.setError(false, true);
		
	}

	
	
	// TODO REMOVE LATER
	//-----------------------------------------------------------
	
	// private String getSelected() {
	// 	return "R1A1";
	// }
	
	// void setError (boolean err, boolean conf) {
	// 	System.out.printf("Error: %s Confirm: %s\n", err, conf);
	// }
		
	//-----------------------------------------------------------
	
}
