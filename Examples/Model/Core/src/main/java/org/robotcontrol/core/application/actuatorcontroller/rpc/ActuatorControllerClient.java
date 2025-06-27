package org.robotcontrol.core.application.actuatorcontroller.rpc;

import actuatorController.Actuator.ActuatorRequest;
import actuatorController.Actuator.Direction;
import actuatorController.ActuatorControlServiceGrpc;
import actuatorController.ActuatorControlServiceGrpc.ActuatorControlServiceBlockingStub;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;

import java.util.concurrent.TimeUnit;

/**
 * gRPC‑Client für einen einzelnen Aktor‑Server.
 * Erwartet beim Bau Host + Port des Ziel‑Aktorservers
 * (z. B. "localhost", 50050).
 */
public class ActuatorControllerClient implements ActuatorController {

    private final ManagedChannel channel;
    private final ActuatorControlServiceBlockingStub stub;

    public ActuatorControllerClient(String host, int port) {
        this.channel = ManagedChannelBuilder
                .forAddress(host, port)
                .usePlaintext()
                .build();

        // wartet bis die Verbindung steht und setzt 3‑Sek‑Deadline
        this.stub = ActuatorControlServiceGrpc
                .newBlockingStub(channel)
                .withWaitForReady()
                .withDeadlineAfter(3, TimeUnit.SECONDS);
    }

    @Override
    public void move(ActuatorDirection ad) {
        ActuatorRequest req = ActuatorRequest.newBuilder()
                .setDirection(Direction.forNumber(ad.ordinal()))
                .build();
        try {
            stub.move(req);  // blocking call mit Deadline
        } catch (StatusRuntimeException ex) {
            System.err.println("move() RPC failed: " + ex.getStatus());
            throw ex;        // ggf. übersetzen in eigene Exception
        }
    }

    /**
     * Channel sauber schließen – z. B. beim Beenden der Anwendung.
     */
    public void shutdown() throws InterruptedException {
        channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
    }
}
