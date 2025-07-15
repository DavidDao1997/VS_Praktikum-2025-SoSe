package org.robotcontrol.core.application.moveadapter;

import org.robotcontrol.core.application.actuatorcontroller.rpc.ActuatorController.ActuatorDirection;
import org.robotcontrol.middlewarev2.Middleware;
import org.robotcontrol.middlewarev2.idl.ActuatorController;
import org.robotcontrol.middlewarev2.idl.StateService;

public class MoveAdapter implements org.robotcontrol.middlewarev2.idl.MoveAdapter {
	private StateService stateService;
	private String selected;

	public MoveAdapter(StateService stateService) {
		this.stateService = stateService;
		selected = "";
	}

	public void move(RobotDirection robotDirection) {
		// String selectedRobot = stateService.getSelected();
		
		
		// if (selectedRobot == null) {
		// 	stateService.setError(true, false);
		// 	return;
		// }
		
		Integer actuatorId;
		ActuatorController.Direction direction;

		switch (robotDirection) {
			case LEFT:
				actuatorId = 1;
				direction = ActuatorController.Direction.DECREASE;
				break;
			case DOWN:
				actuatorId = 3;
				direction = ActuatorController.Direction.DECREASE;
				break;
			case BACKWARD:
				actuatorId = 2;
				direction = ActuatorController.Direction.DECREASE;
				break;
			case CLOSE:		
				actuatorId = 4;
				direction = ActuatorController.Direction.DECREASE;
				break;
			case RIGHT:
				actuatorId = 1;
				direction = ActuatorController.Direction.INCREASE;
				break;
			case UP:
				actuatorId = 3;
				direction = ActuatorController.Direction.INCREASE;
				break;
			case FORWARD:
				actuatorId = 2;
				direction = ActuatorController.Direction.INCREASE;
				break;
			case OPEN:
				actuatorId = 4;
				direction = ActuatorController.Direction.INCREASE;
				break;
			default:
				System.out.println("ILLEGAL MOVEMENT");
				stateService.setError(true, false);
				return;
		}
		
		// FIXME use client
		ActuatorController acm = Middleware.createActuatorControllerClient(selected + "A" + actuatorId);
		acm.move(ActuatorController.Direction.values()[direction.ordinal()]);

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
