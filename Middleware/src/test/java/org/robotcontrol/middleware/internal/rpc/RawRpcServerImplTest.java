package org.robotcontrol.middleware.internal.rpc;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.robotcontrol.middleware.idl.types.RpcValue;
import org.robotcontrol.middleware.internal.rpc.CallableWithTimestamp;
import org.robotcontrol.middleware.internal.rpc.Marshaller;
import org.robotcontrol.middleware.internal.rpc.RawRpcServerImpl;
import org.robotcontrol.middleware.rpc.Callable;

public class RawRpcServerImplTest {

    @Test
    public void testListenAndServe_dispatchesInternalRpcCall() throws Exception {
        int port = getFreePort();

        // Setup mock Callable service
        Callable mockService = mock(Callable.class);

        // Start the server in a new thread
        RawRpcServerImpl server = new RawRpcServerImpl(mockService, port);
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.submit(() -> {
            server.listenAndServe();
        });

        // Give server time to start
        Thread.sleep(200);

        // Send a datagram to trigger request
        DatagramSocket clientSocket = new DatagramSocket();
        byte[] data = Marshaller.marshal("hello", new RpcValue.StringValue("arg")).getBytes();
        DatagramPacket packet = new DatagramPacket(data, data.length, InetAddress.getByName("127.0.0.1"), port);
        clientSocket.send(packet);
        clientSocket.close();

        // Give time for server to receive
        Thread.sleep(200);

        // Verify service was called
        // Capture arguments
        ArgumentCaptor<String> functionCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<RpcValue> argsCaptor = ArgumentCaptor.forClass(RpcValue.StringValue.class);
        verify(mockService, times(1)).call(functionCaptor.capture(), argsCaptor.capture());
        assertEquals("hello", functionCaptor.getValue());

        RpcValue capturedArg = argsCaptor.getValue();
        assertTrue(capturedArg instanceof RpcValue.StringValue);
        assertEquals("arg", RpcValue.unwrap(capturedArg));

        executor.shutdownNow();
    }

    @Test
    public void testListenAndServe_dispatchesRpcCall() throws Exception {
        int port = getFreePort();

        // Setup mock Callable service
        CallableWithTimestamp mockService = mock(CallableWithTimestamp.class);

        // Start the server in a new thread
        RawRpcServerImpl server = new RawRpcServerImpl(mockService, port);
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.submit(() -> {
            server.listenAndServe();
        });

        // Give server time to start
        Thread.sleep(200);

        // Send a datagram to trigger request
        DatagramSocket clientSocket = new DatagramSocket();
        byte[] data = Marshaller.marshal(321L, "hello", new RpcValue.StringValue("arg")).getBytes();
        DatagramPacket packet = new DatagramPacket(data, data.length, InetAddress.getByName("127.0.0.1"), port);
        clientSocket.send(packet);
        clientSocket.close();

        // Give time for server to receive
        Thread.sleep(200);

        // Verify service was called
        // Capture arguments
        ArgumentCaptor<Long> timestampCaptor = ArgumentCaptor.forClass(Long.class);
        ArgumentCaptor<String> functionCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<RpcValue> argsCaptor = ArgumentCaptor.forClass(RpcValue.StringValue.class);
        verify(mockService, times(1)).call(timestampCaptor.capture(), functionCaptor.capture(), argsCaptor.capture());
        assertEquals((Long) 321L, timestampCaptor.getValue());
        assertEquals("hello", functionCaptor.getValue());

        RpcValue capturedArg = argsCaptor.getValue();
        assertTrue(capturedArg instanceof RpcValue.StringValue);
        assertEquals("arg", RpcValue.unwrap(capturedArg));

        executor.shutdownNow();
    }

    private int getFreePort() throws Exception {
        DatagramSocket socket = new DatagramSocket(0);
        int port = socket.getLocalPort();
        socket.close();
        return port;
    }
}
