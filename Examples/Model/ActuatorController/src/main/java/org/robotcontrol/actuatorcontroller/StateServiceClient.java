package org.robotcontrol.actuatorcontroller;


import stateService.StateServiceGrpc;
import stateService.StateServiceGrpc.StateServiceBlockingStub;
import stateService.StateServiceGrpc.StateServiceStub;
import stateService.StateServiceOuterClass.Empty;
import stateService.StateServiceOuterClass.HeartbeatRequest;
import stateService.StateServiceOuterClass.RegisterRequest;
import stateService.StateServiceOuterClass.SelectRequest;
import stateService.StateServiceOuterClass.SelectDirection;
import stateService.StateServiceOuterClass.RobotInfo;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;

import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;


public class StateServiceClient {

    private final ManagedChannel channel;
    private final StateServiceBlockingStub blockingStub;
    private final StateServiceStub asyncStub;

    public StateServiceClient(String host, int port) {
        channel = ManagedChannelBuilder.forAddress(host, port)
                                       .usePlaintext()
                                       .build();

        blockingStub = StateServiceGrpc.newBlockingStub(channel)
                                       .withWaitForReady()
                                       .withDeadlineAfter(3, TimeUnit.SECONDS);

        asyncStub = StateServiceGrpc.newStub(channel)
                                    .withWaitForReady();   // Deadline per Streaming nicht nötig
    }

    /** Registriert einen Aktor beim StateService. */
    public void register(String motorName, String host, int port) {
        RegisterRequest req = RegisterRequest.newBuilder()
                                             .setMotorName(motorName)
                                             .setHost(host)
                                             .setPort(port)
                                             .build();
        call(() -> blockingStub.register(req), "register");
    }

    /** Schickt einen Heartbeat. */
    public void heartbeat(String motorName) {
        HeartbeatRequest req = HeartbeatRequest.newBuilder()
                                               .setMotorName(motorName)
                                               .build();
        call(() -> blockingStub.heartbeat(req), "heartbeat");
    }


    /** Channel sauber schließen – call bei Programmende */
    public void shutdown() throws InterruptedException {
        channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
    }

    private void call(Runnable r, String what) {
        try {
            r.run();
        } catch (StatusRuntimeException ex) {
            System.err.println(what + " RPC failed: " + ex.getStatus());
            throw ex;
        }
    }
}