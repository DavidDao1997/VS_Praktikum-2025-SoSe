package org.robotcontrol.middleware.idl;

public interface MoveAdapter {
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

    public void move(RobotDirection robotDirection);
}
