package org.robotcontrol.middleware.services;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.robotcontrol.middleware.RpcValue;
import org.robotcontrol.middleware.ServerStub;

import actuatorController.Actuator.ActuatorMoveResponse;
import actuatorController.Actuator.ActuatorRequest;
import actuatorController.ActuatorControlServiceGrpc.ActuatorControlServiceImplBase;
import io.grpc.Status;
import io.grpc.stub.ServerCallStreamObserver;
import io.grpc.stub.StreamObserver;

public class ActuatorControllerServer extends ActuatorControlServiceImplBase {

    private final ServerStub actuatorController;
    private final Map<String, Method> methods;

    public ActuatorControllerServer(ServerStub actuatorController) {
        this.actuatorController = actuatorController;
        methods = new HashMap<>();
        // populate methods map by reflection from target class
        for (Method m : actuatorController.getClass().getMethods()) {
            methods.put(m.getName(), m);
        }
    }

  @Override
public void move(ActuatorRequest request,
                 StreamObserver<ActuatorMoveResponse> responseObserver) {

    ServerCallStreamObserver<ActuatorMoveResponse> obs =
        (ServerCallStreamObserver<ActuatorMoveResponse>) responseObserver;

    // Sofort raus, falls der Client den Call schon storniert hat
    if (obs.isCancelled()) {
        System.out.println("Client already cancelled.");
        return;
    }

    try {
        // Business-Logik
        actuatorController.call("move",
                new RpcValue.IntValue(request.getDirectionValue()));

        // Antwort senden
        obs.onNext(
            ActuatorMoveResponse.newBuilder()
                                .setSuccess(true)
                                .build());

        // Kann inzwischen gecancelt worden sein
        if (!obs.isCancelled()) {
            obs.onCompleted();   // genau EIN Versuch
        }

    } catch (Exception ex) {
        // Nur melden, wenn Stream noch offen
        if (!obs.isCancelled()) {
            System.err.println("Move failed: " + ex);
            // Kein onError() mehr – vermeidet erneutes close()
        } else {
            System.out.println("Exception after cancel: " + ex);
        }
    }
}
}
