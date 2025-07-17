package org.robotcontrol.middleware.internal.rpc;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.robotcontrol.middleware.idl.types.RpcValue;
import org.robotcontrol.middleware.internal.rpc.Marshaller;
import org.robotcontrol.middleware.internal.rpc.RpcRequest;

@RunWith(Parameterized.class)
public class MarshallerTest {

    @Parameterized.Parameters(name = "{index}: fn={1}, values={2}, timestamp={0}")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
            // No timestamp
            {null, "echo", Arrays.asList(new RpcValue.StringValue("hello"))},
            {null, "sum", Arrays.asList(new RpcValue.LongValue(42), new RpcValue.LongValue(8))},
            {null, "toggle", Arrays.asList(new RpcValue.BoolValue(true))},
            {null, "emptyList", Arrays.asList(new RpcValue.ListValue(Collections.emptyList()))},
            {null, "nestedList", Arrays.asList(new RpcValue.ListValue(Arrays.asList(
                new RpcValue.LongValue(1), new RpcValue.BoolValue(false))))},

            // With timestamp
            {123456L, "timedEcho", Arrays.asList(new RpcValue.StringValue("withTime"))},
            {999999L, "complex", Arrays.asList(
                new RpcValue.StringValue("str"),
                new RpcValue.BoolValue(true),
                new RpcValue.LongValue(5),
                new RpcValue.ListValue(Arrays.asList(
                    new RpcValue.StringValue("a"),
                    new RpcValue.LongValue(2)
                ))
            )},

            // Bitmap value
            {null, "bitmapTest", Arrays.asList(
                new RpcValue.Bitmap256Value(paddedBytes(new byte[]{1, 2, 3, (byte) 255}))
            )}
        });
    }

    @Parameterized.Parameter(0)
    public Long timestamp;

    @Parameterized.Parameter(1)
    public String functionName;

    @Parameterized.Parameter(2)
    public List<RpcValue> values;

    @Test
    public void testMarshalAndUnmarshalSymmetry() {
        // Marshal
        String json = Marshaller.marshal(timestamp, functionName, values.toArray(new RpcValue[0]));

        // Unmarshal
        RpcRequest result = Marshaller.unmarshal(json);

        // Assertions
        assertEquals("Function name should match", functionName, result.function());
        assertEquals("Timestamp should match", timestamp, result.timestamp());
        assertEquals("Value list size should match", values.size(), result.values().size());

        for (int i = 0; i < values.size(); i++) {
            Object val = RpcValue.unwrap(values.get(i));
            Object res = RpcValue.unwrap(result.values().get(i)); 
            
            if (val instanceof byte[] && res instanceof byte[]) {
                assertTrue("Value at index " + i + " should match (byte array)", 
                    Arrays.equals((byte[]) val, (byte[]) res));
            } else {
                assertEquals("Value at index " + i + " should match", val, res);
            }
        }
    }

    private static byte[] paddedBytes(byte[] input) {
        byte[] result = new byte[32]; // 256 bits
        System.arraycopy(input, 0, result, 0, Math.min(input.length, 32));
        return result;
    }
}