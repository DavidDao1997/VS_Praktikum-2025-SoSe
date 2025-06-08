package com.start.client;
import com.start.ControllerProto.RobotStatusRequest;
import com.start.ControllerProto.RobotStatusAck;
import com.start.RobotControllerGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

public class GrpcClient {
    public static void main(String[] args) {
        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 50051)
                .usePlaintext()
                .build();

        RobotControllerGrpc.RobotControllerBlockingStub stub = RobotControllerGrpc.newBlockingStub(channel);

        RobotStatusRequest request = RobotStatusRequest.newBuilder()
                .addRobots("Robo1")
                .addRobots("Robo2")
                .setCommand("START")
                .setIsError(false)
                .setIsConfirmed(true)
                .build();

        RobotStatusAck ack = stub.updateRobotStatus(request);
        System.out.println("Antwort vom Controller: received = " + ack.getReceived());

        channel.shutdown();
    }
}
