package org.robotcontrol.core.application.moveadapter;

import org.robotcontrol.core.application.actuatorcontroller.rpc.ActuatorController;
import org.robotcontrol.core.application.actuatorcontroller.rpc.ActuatorController.ActuatorDirection;
import org.robotcontrol.core.application.actuatorcontroller.rpc.ActuatorControllerMock;
import org.robotcontrol.core.application.stateservice.StateService;
import org.robotcontrol.middleware.actuatorcontroller.ActuatorControllerClient;
import org.robotcontrol.middleware.idl.ActuatorController.Direction;

public class MoveAdapter implements org.robotcontrol.middleware.idl.MoveAdapter {
	private StateService stateService;
	private String selected;

	public MoveAdapter(StateService stateService) {
		this.stateService = stateService;
		selected = "";
	}

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
				actuatorId = 3;
				direction = ActuatorDirection.DECREASE;
				break;
			case BACKWARD:
				actuatorId = 2;
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
				actuatorId = 3;
				direction = ActuatorDirection.INCREASE;
				break;
			case FORWARD:
				actuatorId = 2;
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
		org.robotcontrol.middleware.idl.ActuatorController acm = new ActuatorControllerClient(selected + "A" + actuatorId);
		acm.move(Direction.values()[direction.ordinal()]);

		stateService.setError(false, true);
		
	}

	@Override
	public void setSelected(String selected) {
		this.selected = selected; 
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
