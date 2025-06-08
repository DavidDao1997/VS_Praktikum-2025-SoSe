package com.start;

import com.start.ControllerProto.RobotStatusAck;
import com.start.ControllerProto.RobotStatusRequest;

import com.start.view.ViewProto;
import com.start.view.RobotViewGrpc;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;

public class ControllerServer {

    public static void main(String[] args) throws Exception {
        Server server = ServerBuilder.forPort(50051)
                .addService(new RobotControllerImpl())
                .build()
                .start();

        System.out.println("Controller-Server läuft auf Port 50051");
        server.awaitTermination();
    }

    static class RobotControllerImpl extends RobotControllerGrpc.RobotControllerImplBase {
        @Override
        public void updateRobotStatus(RobotStatusRequest request, StreamObserver<RobotStatusAck> responseObserver) {
            System.out.println("Controller empfängt Request von Model/Client: " + request.getCommand());

            // Weiterleitung an View-Service via gRPC
            ManagedChannel viewChannel = ManagedChannelBuilder
                    .forAddress("localhost", 6000)
                    .usePlaintext()
                    .build();

            RobotViewGrpc.RobotViewBlockingStub viewStub = RobotViewGrpc.newBlockingStub(viewChannel);

            // View erwartet denselben RobotStatusRequest
            ViewProto.ViewResponse viewResponse = viewStub.showRobotStatus(request);

            viewChannel.shutdown();

            // Controller gibt ACK zurück
            RobotStatusAck ack = RobotStatusAck.newBuilder()
                    .setReceived(viewResponse.getDisplayed())
                    .build();

            responseObserver.onNext(ack);
            responseObserver.onCompleted();
        }
    }
}
