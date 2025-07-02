package org.robotcontrol.core.application.moveadapter;

import org.robotcontrol.core.application.actuatorcontroller.rpc.ActuatorController;
import org.robotcontrol.core.application.actuatorcontroller.rpc.ActuatorController.ActuatorDirection;
import org.robotcontrol.core.application.actuatorcontroller.rpc.ActuatorControllerMock;
import org.robotcontrol.core.application.stateservice.StateService;

import lombok.AllArgsConstructor;


@AllArgsConstructor
public class MoveAdapter implements org.robotcontrol.middleware.idl.MoveAdapter {
	private StateService stateService;

	public void move(RobotDirection robotDirection) {
		String selectedRobot = stateService.getSelected();
		
		
		if (selectedRobot == null) {
			stateService.setError(true, false);
			return;
		}
		
		Integer actuatorId;
		ActuatorDirection direction;

		switch (robotDirection) {
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
