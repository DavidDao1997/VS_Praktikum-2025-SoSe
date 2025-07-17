package org.robotcontrol.core.application.moveadapter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.robotcontrol.middleware.Middleware;
import org.robotcontrol.middleware.idl.ActuatorController;
import org.robotcontrol.middleware.idl.StateService;
import org.robotcontrol.middleware.utils.Logger;

public class MoveAdapter implements org.robotcontrol.middleware.idl.MoveAdapter {
	private static final Logger logger = new Logger("MoveAdapter");
	private StateService stateService;
	private String selected;
	private final Map<String, ActuatorController> clients = new ConcurrentHashMap<>();

	public MoveAdapter(StateService stateService) {
		this.stateService = stateService;
		selected = "";
	}

	public void move(RobotDirection robotDirection) {
		if (selected.isEmpty()) return;
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
		ActuatorController acm = clients.get(selected + "A" + actuatorId);
		if (acm == null) {
			acm = Middleware.createActuatorControllerClient(selected + "A" + actuatorId);
			clients.put(selected + "A" + actuatorId, acm);
		}
		logger.info("TRYING TO MOVE actuator: %s, direction: %s", selected + "A" + actuatorId, direction);
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
