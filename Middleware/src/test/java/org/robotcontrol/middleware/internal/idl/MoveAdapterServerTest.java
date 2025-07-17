package org.robotcontrol.middleware.internal.idl;

import org.junit.Test;
import org.robotcontrol.middleware.idl.MoveAdapter;
import org.robotcontrol.middleware.rpc.RpcServer;

import static org.mockito.Mockito.*;

public class MoveAdapterServerTest {

    // @Test
    // public void testServerDelegation() {
    //     RpcServer mockServer = mock(RpcServer.class);

    //     MoveAdapter dummyAdapter = mock(MoveAdapter.class);
    //     MoveAdapterImpl.Server server = new MoveAdapterImpl.Server(1234, dummyAdapter, "MoveAdapter", "move", "setSelected");

    //     // Inject mock using reflection
    //     setPrivateField(server, "server", mockServer);

    //     server.start();
    //     verify(mockServer).start();

    //     server.listenAndServe();
    //     verify(mockServer).listenAndServe();

    //     server.stop();
    //     verify(mockServer).stop();

    //     verifyNoMoreInteractions(mockServer);
    // }

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
