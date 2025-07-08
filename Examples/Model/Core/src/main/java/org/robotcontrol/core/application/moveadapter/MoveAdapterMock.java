package org.robotcontrol.core.application.moveadapter;

import org.robotcontrol.middleware.idl.MoveAdapter;
import org.robotcontrol.middleware.utils.Logger;

import lombok.Data;

@Data
public class MoveAdapterMock implements MoveAdapter {
    private final Logger logger = new Logger("MoveAdapterMock");
    private String selected = ""; 

    @Override
    public void move(RobotDirection robotDirection) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'move'");
    }

    @Override
    public void setSelected(String selected) {
        this.selected = selected;
    }
}
