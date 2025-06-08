package com.start;

import com.start.ControllerProto.RobotStatusRequest;
import com.start.ControllerProto.RobotStatusAck;
import com.start.RobotControllerGrpc;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class GrpcControllerTest {

    private ManagedChannel channel;
    private RobotControllerGrpc.RobotControllerBlockingStub stub;

    @Before
    public void setUp() {
        channel = ManagedChannelBuilder.forAddress("localhost", 50051)
                .usePlaintext()
                .build();

        stub = RobotControllerGrpc.newBlockingStub(channel);
    }

    @After
    public void tearDown() {
        channel.shutdown();
    }

    @Test
    public void testUpdateRobotStatus_success() {
        // Arrange
        RobotStatusRequest request = RobotStatusRequest.newBuilder()
                .addRobots("TestBot1")
                .addRobots("TestBot2")
                .setCommand("RUN")
                .setIsError(false)
                .setIsConfirmed(true)
                .build();

        // Act
        RobotStatusAck response = stub.updateRobotStatus(request);

        // Assert
        assertTrue("View should acknowledge receipt", response.getReceived());
        System.out.println("Test erfolgreich: Antwort = " + response.getReceived());
    }
}
