package org.robotcontrol.middlewarev2.internal.idl;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.robotcontrol.middlewarev2.idl.MoveAdapter;
import org.robotcontrol.middlewarev2.idl.MoveAdapter.RobotDirection;
import org.robotcontrol.middlewarev2.idl.types.RpcValue;

public class MoveAdapterServiceTest {
    static class TestCase {
        final String fnName;
        final RpcValue[] args;
        final Runnable verifyCall;

        TestCase(String fnName, RpcValue[] args, Runnable verifyCall) {
            this.fnName = fnName;
            this.args = args;
            this.verifyCall = verifyCall;
        }
    }

    @Test
    public void testServiceCall() {
        final MoveAdapter mockAdapter = mock(MoveAdapter.class);
        MoveAdapterImpl.Service service = new MoveAdapterImpl.Service(mockAdapter);

        List<TestCase> testCases = Arrays.asList(
            new TestCase("move", new RpcValue[]{
                new RpcValue.LongValue(RobotDirection.LEFT.ordinal())
            }, new Runnable() {
                public void run() {
                    verify(mockAdapter).move(RobotDirection.LEFT);
                    verifyNoMoreInteractions(mockAdapter);
                }
            }),

            new TestCase("setSelected", new RpcValue[]{
                new RpcValue.StringValue("robot42")
            }, new Runnable() {
                public void run() {
                    verify(mockAdapter).setSelected("robot42");
                    verifyNoMoreInteractions(mockAdapter);
                }
            })
        );

        for (TestCase tc : testCases) {
            reset(mockAdapter); // isolate test cases
            service.call(tc.fnName, tc.args);
            tc.verifyCall.run();
        }
    }
}

