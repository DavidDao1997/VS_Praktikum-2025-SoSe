package org.robotcontrol.middlewarev2.internal.idl;

import org.junit.Test;
import org.robotcontrol.middlewarev2.idl.MoveAdapter.RobotDirection;
import org.robotcontrol.middlewarev2.idl.types.RpcValue;
import org.robotcontrol.middlewarev2.rpc.Invokable;

import static org.mockito.Mockito.*;

public class MoveAdapterClientTest {

    // @Test
    // public void testClientMoveAndSetSelected() {
    //     Invokable mockClient = mock(Invokable.class);
    //     MoveAdapterImpl.Client client = new MoveAdapterImpl.Client();
    //     // Inject mock
    //     setPrivateField(client, "client", mockClient);

    //     client.move(RobotDirection.RIGHT);
    //     verify(mockClient).invoke("move", new RpcValue.LongValue(RobotDirection.RIGHT.ordinal()));

    //     client.setSelected("robot99");
    //     verify(mockClient).invoke("setSelected", new RpcValue.StringValue("robot99"));

    //     verifyNoMoreInteractions(mockClient);
    // }

    // // Use reflection to inject mock into private field
    // private void setPrivateField(Object target, String fieldName, Object value) {
    //     try {
    //         java.lang.reflect.Field field = target.getClass().getDeclaredField(fieldName);
    //         field.setAccessible(true);
    //         field.set(target, value);
    //     } catch (Exception e) {
    //         throw new RuntimeException(e);
    //     }
    // }
}
