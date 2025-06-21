package org.robotcontrol.middleware.services;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.robotcontrol.middleware.RpcValue;
import org.robotcontrol.middleware.ServerStub;

import io.grpc.stub.StreamObserver;
import movementAdapter.MoveAdapterGrpc.MoveAdapterImplBase;
import movementAdapter.MoveAdapterOuterClass.MoveRequest;
import movementAdapter.MoveAdapterOuterClass.MoveResponse;

public class MoveAdapterServer extends MoveAdapterImplBase {
    private ServerStub moveAdapterService;
    private Map<String, Method> methods;

    public MoveAdapterServer(ServerStub moveAdapterService) {
        this.moveAdapterService = moveAdapterService;
        methods = new HashMap<>();
        // populate methods map by reflection from target class
        for (Method m : moveAdapterService.getClass().getMethods()) {
            methods.put(m.getName(), m);
        }
    }

    @Override
    public void move(MoveRequest request, StreamObserver<MoveResponse> responseObserver) {
        System.out.printf("MoveAdapterService.move() was called with: %s\n", request.getDirection());
        moveAdapterService.call("move", new RpcValue.IntValue(request.getDirectionValue()));
        
        responseObserver.onNext(MoveResponse.newBuilder().setSuccess(true).build());
        responseObserver.onCompleted();
    }
}
