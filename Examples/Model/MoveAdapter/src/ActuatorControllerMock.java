public class ActuatorControllerMock {
	
	
	public enum ActuatorDirection{
		INCREASE,
		DECREASE
	}
	
	String name;
	
	public ActuatorControllerMock(String name) {
		this.name = name;
	}
	
	
	
	
	public void move(ActuatorDirection ad) {
		System.out.printf("%s\n", ad);		
	}

}
