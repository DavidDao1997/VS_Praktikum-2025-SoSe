package org.robotcontrol.core.application.stateservice;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(of = "name")
public class Robot {
	private String name;
	
	private boolean A1;
	private boolean A2;
	private boolean A3;
	private boolean A4;
	
	public Robot(String name){
		this.name = name;
		A1 = A2 = A3 = A4 = false;
	}
	
	public boolean isAvailable() {
		return A1 && A2 && A3 && A4;
	}
}
