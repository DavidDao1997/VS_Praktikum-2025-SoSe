import java.util.ArrayList;
import java.util.List;

public class StateService {
	
	public enum SelectDirection{
		UP,
		DOWN		
	}
	
	
	int selectedRobot;
	boolean error;
	boolean confirm;
	private List<Robot> registeredRobots;
	private List<Robot> availableRobots;
	
	
	public StateService() {
		registeredRobots = new ArrayList<Robot>();
		availableRobots = new ArrayList<Robot>();
		selectedRobot = 0;
		error = false;
		confirm = false;
		
	}
	
	public void register(String motorName) {
		String robotName = motorName.substring(0,2);
		Robot r = new Robot(robotName);
		
		
		if (!registeredRobots.stream().map(Robot::getName).toList().contains(robotName)) {
			registeredRobots.add(r);
		}
		int idx = registeredRobots.stream().map(Robot::getName).toList().indexOf(robotName);
		r = registeredRobots.get(idx);
		
		switch (motorName.substring(2, 4)) {
			case "A1": 
				r.setA1(true);
				break;
			case "A2":
				r.setA2(true);
				break;
			case "A3":
				r.setA3(true);
				break;
			case "A4":
				r.setA4(true);
				break;
			
			default:
				throw new IllegalArgumentException("Unexpected value: " + motorName.substring(2, 4));
		}
		// check if availabeRobots can be updated
		if (r.isAvailable()) {
			availableRobots.add(r);
			
			// send update do not update error, selectedRobot
			sendUpdate();
		}
		
		
		
		
	}
	
//	public void heartbeat(String motorName) {
//		
//	}
	
	public void subscribe() {
		
	}
	
	public void select(SelectDirection sd) {
		if (availableRobots.isEmpty()) {
			error = true;
			confirm = false;
			sendUpdate();
			return;
		}
		
		if (sd == SelectDirection.UP) {
			selectedRobot = (selectedRobot - 1 + availableRobots.size() + 1) % (availableRobots.size() + 1);
		} else if (sd == SelectDirection.DOWN) {
			selectedRobot = (selectedRobot + 1) % (availableRobots.size() + 1);
		}
		confirm = true;
		error = false;
		// send update
		sendUpdate();
		
		
		
				
	}
	
	public String getSelected() {
		if (selectedRobot > 0) {
			return availableRobots.get(selectedRobot).getName();
		}
		return null;
	}
	
	public void setError(boolean err) {
		if (error != err) {
			error = err;
		//  send update
			sendUpdate();			
		}
	}
	
	
	private void sendUpdate() {
		String[] availRobots = new String[availableRobots.size()+1];
		availRobots[0] = null;
		int idx = 1;
		
		for (Robot robot : availableRobots) {
			availRobots[idx] = robot.getName();
			idx++;
		}
		
		update(availRobots, selectedRobot, error, confirm);
	}
	
	// TODO REMOVE LATER BECAUSE THIS IS THE CONTROLLER IMPLEMENTAION
	public void update(String[] robots, int selected, boolean error, boolean confirm) {
		// updateView(robots, selected, error, confirm);
		System.out.println("--------------------------------------------------");
		for (int i = 0; i < robots.length; i++) {
			System.out.printf("Robot %d: %s\n", i,robots[i]);
		}
		System.out.println(selected);
		System.out.println(error);
		System.out.println(confirm);
		System.out.println("--------------------------------------------------");
	}
		

}
