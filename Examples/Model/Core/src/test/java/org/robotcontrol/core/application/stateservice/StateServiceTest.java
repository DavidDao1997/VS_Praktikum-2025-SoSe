package org.robotcontrol.core.application.stateservice;

import org.junit.Test;
import org.robotcontrol.core.application.controller.rpc.ControllerMock;
import org.robotcontrol.core.application.moveadapter.MoveAdapterMock;
import org.robotcontrol.middleware.idl.MoveAdapter;
import org.robotcontrol.middleware.idl.StateService.SelectDirection;

import static org.junit.Assert.assertEquals;

import org.junit.Before;

public class StateServiceTest {
    private StateService stateService;
    private MoveAdapterMock moveAdapter;
    @Before
    public void setup() {
        stateService = new StateService(new ControllerMock());
        moveAdapter = new MoveAdapterMock();
        stateService.setMoveAdapter(moveAdapter);
    }

    @Test
    public void testSelectPropagation() {
        stateService.registerActuator("R1A1", true);
        stateService.registerActuator("R1A2", true);
        stateService.registerActuator("R1A3", true);
        stateService.registerActuator("R1A4", true);
        stateService.registerActuator("R2A1", true);
        stateService.registerActuator("R2A2", true);
        stateService.registerActuator("R2A3", true);
        stateService.registerActuator("R2A4", true);

        assertEquals("", moveAdapter.getSelected());

        stateService.select(SelectDirection.DOWN);

        assertEquals("R1", moveAdapter.getSelected());

        stateService.select(SelectDirection.DOWN);

        assertEquals("R2", moveAdapter.getSelected());

        stateService.select(SelectDirection.DOWN);

        assertEquals("", moveAdapter.getSelected());
    }

}
