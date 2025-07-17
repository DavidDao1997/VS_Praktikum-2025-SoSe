package org.robotcontrol.core.application.actuatorcontroller.rpc;

public class ActuatorControllerMock {
	String name;
	
	public ActuatorControllerMock(String name) {
		this.name = name;
	}
	
	public void move(ActuatorController.ActuatorDirection ad) {
		System.out.printf("ActuatorControllerMock{name: %s}.move() was called with: %s\n", name, ad);		
	}
}
