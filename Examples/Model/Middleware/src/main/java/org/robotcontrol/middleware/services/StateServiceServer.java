package org.robotcontrol.middleware.services;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.robotcontrol.middleware.RpcValue;
import org.robotcontrol.middleware.ServerStub;

import io.grpc.stub.StreamObserver;
import stateService.StateServiceGrpc;
import stateService.StateServiceOuterClass.Empty;
import stateService.StateServiceOuterClass.HeartbeatRequest;
import stateService.StateServiceOuterClass.RegisterRequest;
import stateService.StateServiceOuterClass.SelectRequest;


public class StateServiceServer extends StateServiceGrpc.StateServiceImplBase {
    private ServerStub stateService;
    private Map<String, Method> methods;

    public StateServiceServer(ServerStub stateService) {
        this.stateService = stateService;
        methods = new HashMap<>();
        // populate methods map by reflection from target class
        for (Method m : stateService.getClass().getMethods()) {
            methods.put(m.getName(), m);
        }
    }

    @Override
    public void select(SelectRequest request, StreamObserver<Empty> responseObserver) {
        System.out.printf("StateServiceServer.select() was called with: %s\n", request.getSelectDirection());
        stateService.call("select", new RpcValue.IntValue(request.getSelectDirectionValue()));

        responseObserver.onNext(Empty.newBuilder().build());
        responseObserver.onCompleted();
    }

    @Override
    public void register(RegisterRequest request, StreamObserver<Empty> responseObserver) {
        System.out.printf("StateServiceServer.register() was called with: %\n", request.getMotorName());
        stateService.call("register", new RpcValue.StringValue(request.getMotorName()));

        responseObserver.onNext(Empty.newBuilder().build());
        responseObserver.onCompleted();
    }

    @Override
    public void heartbeat(HeartbeatRequest request, StreamObserver<Empty> responseObserver) {
        System.out.printf("StateServiceServer.heartbeat() was called with: %s\n", request.getMotorName());
        stateService.call("heartbeat", new RpcValue.StringValue(request.getMotorName()));

        responseObserver.onNext(Empty.newBuilder().build());
        responseObserver.onCompleted();
    }
}
