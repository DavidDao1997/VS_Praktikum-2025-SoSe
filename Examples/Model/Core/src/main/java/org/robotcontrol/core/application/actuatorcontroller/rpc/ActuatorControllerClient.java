package org.robotcontrol.core.application.actuatorcontroller.rpc;

import actuatorController.Actuator.ActuatorRequest;
import actuatorController.Actuator.Direction;
import actuatorController.ActuatorControlServiceGrpc.ActuatorControlServiceBlockingStub;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import java.util.concurrent.TimeUnit;  // für Deadline

import static actuatorController.ActuatorControlServiceGrpc.newBlockingStub;

public class ActuatorControllerClient implements ActuatorController {
    private ActuatorControlServiceBlockingStub stub;

    public ActuatorControllerClient() {
        ManagedChannel channel = ManagedChannelBuilder
            .forAddress("localhost", 50050)
            .usePlaintext()
            .build();
        // Stub mit 10 Sekunden Deadline
        stub = newBlockingStub(channel);
    }

   @Override
public void move(ActuatorDirection ad) {
    ActuatorRequest req = ActuatorRequest.newBuilder()
                                         .setDirection(Direction.forNumber(ad.ordinal()))
                                         .build();
    try {
        stub.move(req);                       // blocking call
    } catch (io.grpc.StatusRuntimeException ex) {
        System.err.println("RPC failed: " + ex.getStatus());
        throw ex;                             // oder übersetzen in eigene Exception
    }
}
}
