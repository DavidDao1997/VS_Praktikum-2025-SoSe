package com.start.view;

import com.start.ControllerProto.RobotStatusRequest;
import com.start.view.ViewProto.ViewResponse;
import com.start.view.RobotViewGrpc;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;

public class ViewServer {

    public static void main(String[] args) throws Exception {
        Server server = ServerBuilder.forPort(6000)
                .addService(new RobotViewImpl())
                .build()
                .start();

        System.out.println("View-Server läuft auf Port 6000");
        server.awaitTermination();
    }

    static class RobotViewImpl extends RobotViewGrpc.RobotViewImplBase {
        @Override
        public void showRobotStatus(RobotStatusRequest request, StreamObserver<ViewResponse> responseObserver) {
            System.out.println("View hat erhalten:");
            System.out.println("- Command: " + request.getCommand());
            System.out.println("- Robots: " + request.getRobotsList());
            System.out.println("- isError: " + request.getIsError());
            System.out.println("- isConfirmed: " + request.getIsConfirmed());

            ViewResponse response = ViewResponse.newBuilder()
                    .setDisplayed(true)
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();
        }
    }
}
