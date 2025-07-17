package org.robotcontrol.middleware.internal.rpc;

import static org.junit.Assert.assertEquals;

import java.net.DatagramPacket;
import java.net.DatagramSocket;

import org.junit.Test;
import org.robotcontrol.middleware.idl.types.RpcValue;
import org.robotcontrol.middleware.internal.rpc.RawRpcClientImpl;

public class RawRpcClientImplTest {

    @Test
    public void testInvokeExternal_sendsDatagram() throws Exception {
        // Setup a test server socket to receive the datagram
        DatagramSocket serverSocket = new DatagramSocket(0);
        int port = serverSocket.getLocalPort();
        String ip = "127.0.0.1";

        // TODO test with real timestamp instad of hardcoded 123

        // Stub Marshaller (replace with your own stubbing mechanism)
        final String expectedPayload = "{\"timeStamp\":123,\"function\":\"someFunction\",\"params\":[\"test\"]}";
        // MarshallerStub.setMarshalOutput(expectedPayload); // assume this hook exists for testing

        RawRpcClientImpl client = new RawRpcClientImpl(ip + ":" + port);
        client.invoke(123L, "someFunction", new RpcValue.StringValue("test"));

        // Receive packet
        byte[] buffer = new byte[256];
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
        serverSocket.receive(packet);
        String received = new String(packet.getData(), 0, packet.getLength());

        assertEquals(expectedPayload, received);

        serverSocket.close();
    }

    @Test
    public void testInvoke_sendsDatagram() throws Exception {
        // Setup a test server socket to receive the datagram
        DatagramSocket serverSocket = new DatagramSocket(0);
        int port = serverSocket.getLocalPort();
        String ip = "127.0.0.1";

        // Stub Marshaller (replace with your own stubbing mechanism)
        final String expectedPayload = "{\"function\":\"someFunction\",\"params\":[\"test\"]}";
        // MarshallerStub.setMarshalOutput(expectedPayload); // assume this hook exists for testing

        RawRpcClientImpl client = new RawRpcClientImpl(ip + ":" + port);
        client.invoke("someFunction", new RpcValue.StringValue("test"));

        // Receive packet
        byte[] buffer = new byte[256];
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
        serverSocket.receive(packet);
        String received = new String(packet.getData(), 0, packet.getLength());

        assertEquals(expectedPayload, received);

        serverSocket.close();
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void testConstructor_invalidSocketFormat_throwsIndexOutOfBounds() {
        new RawRpcClientImpl("127.0.0.1");
    }

    @Test(expected = RuntimeException.class)
    public void testConstructor_invalidHost_throwsRuntimeException() {
        new RawRpcClientImpl("not.an.ip:1234");
    }
}
