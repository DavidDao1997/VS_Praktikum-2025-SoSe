
public class MoveAdapter {
	
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
	
	
	public void move(RobotDirection rd) {
		String selectedRobot = getSelected();
		
		// TODO NOT FOR RPC
		ActuatorControllerMock acm = new ActuatorControllerMock(selectedRobot);
		
		if (selectedRobot.isBlank()) {
			setError(true, false);
			return;
		}
		
		switch (rd) {
			case LEFT:
			case DOWN:
			case BACKWARD:
			case CLOSE:		
				// TODO NOT FOR RPC
				acm.move(ActuatorControllerMock.ActuatorDirection.DECREASE);
				break;
			case RIGHT:
			case UP:
			case FORWARD:
			case OPEN:
				// TODO NOT FOR RPC
				acm.move(ActuatorControllerMock.ActuatorDirection.INCREASE);
				break;

			default:
				System.out.println("ILLEGAL MOVEMENT");
				setError(true, false);
				break;
		}
		setError(false, true);
		
	}

	
	
	// TODO REMOVE LATER
	//-----------------------------------------------------------
	
	private String getSelected() {
		return "R1A1";
	}
	
	void setError (boolean err, boolean conf) {
		System.out.printf("Error: %s Confirm: %s\n", err, conf);
	}
	
	
	
	
	
	//-----------------------------------------------------------
	
}
