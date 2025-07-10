package org.robotcontrol.middleware.idl;

public interface MoveAdapter {
    public enum RobotDirection{
		UP,
		DOWN,
		LEFT,
		RIGHT,
		FORWARD,
		BACKWARD,
		OPEN,
		CLOSE
	}

    public void move(RobotDirection robotDirection);
	public void setSelected(String selected);
}
