package org.robotcontrol.middleware.internal.timestamp;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.robotcontrol.middleware.idl.types.RpcValue;
import org.robotcontrol.middleware.internal.timestamp.TimestampServiceImpl;

public class TimestampServiceImplTest {
    @Test
    public void testSetAndGetTimestamp() {
        TimestampServiceImpl service = new TimestampServiceImpl();
        service.call(
            "setTimestamp", 
            new RpcValue.StringValue("someService"),
            new RpcValue.StringValue("someFunc"),
            new RpcValue.LongValue(0)
        );

        Long int32Max = 4294967295L;
        service.call(
            "setTimestamp", 
            new RpcValue.StringValue("someService"),
            new RpcValue.StringValue("someOtherFunc"),
            new RpcValue.LongValue(int32Max)
        );

        Long result = service.getTimestamp("someService", "someFunc");
        assertEquals((Long) 0L, result);
        Long result2 = service.getTimestamp("someService", "someOtherFunc");
        assertEquals(int32Max, result2);
    }
}
