package org.robotcontrol.middleware.idl;

public interface MoveAdapter {
    public enum Direction{
		LEFT,
		RIGHT,
		UP,
		DOWN,
		BACKWARD,
		FORWARD,
		OPEN,
		CLOSE
	}

    public void move(Direction robotDirection);
}
