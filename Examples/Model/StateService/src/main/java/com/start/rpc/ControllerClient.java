package com.start.rpc;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

public class ControllerClient implements controller {

    ControllerClient() {
        // ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 50051)
        //         .usePlaintext()
        //         .build();

        // RobotControllerGrpc.RobotControllerBlockingStub stub = RobotControllerGrpc.newBlockingStub(channel);


    }

    @Override
    public void update(String[] robots, int selected, boolean error, boolean confirm) {
        // future invoce call here
        
        // controller.update(robots,int,boolean,boolean);
        
    }

    // private invoke(...)
}
