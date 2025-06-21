package org.robotcontrol.core.application.controller.rpc;


import static com.start.RobotControllerGrpc.newBlockingStub;

import java.util.Arrays;

import com.start.ControllerProto;
import com.start.RobotControllerGrpc.RobotControllerBlockingStub;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;


public class ControllerClient implements Controller {
    private  RobotControllerBlockingStub stub;

    public ControllerClient() {
        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 50051)
                .usePlaintext()
                 .build();

         stub = newBlockingStub(channel);
    }

    @Override
    public void update(String[] robots, int selected, boolean error, boolean confirm) {
        // future invoce call here
        System.out.printf("Lenght of robots: %d\n",robots.length);
        System.out.printf("Index of selected: %d\n",selected);

        ControllerProto.RobotStatusRequest request = ControllerProto.RobotStatusRequest.newBuilder()
                .addAllRobots(Arrays.stream(robots).toList())
                .setCommand(robots[selected])
                .setIsError(error)
                .setIsConfirmed(confirm)
                .build();

        ControllerProto.RobotStatusAck ack = stub.updateRobotStatus(request);
        // controller.update(robots,int,boolean,boolean);
        
    }

    // private invoke(...)
}
