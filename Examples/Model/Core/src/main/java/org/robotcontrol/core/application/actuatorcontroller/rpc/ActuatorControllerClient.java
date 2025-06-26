package org.robotcontrol.core.application.actuatorcontroller.rpc;

import actuatorController.Actuator.ActuatorRequest;
import actuatorController.Actuator.Direction;
import actuatorController.ActuatorControlServiceGrpc.ActuatorControlServiceBlockingStub;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import lombok.AllArgsConstructor;
import static actuatorController.ActuatorControlServiceGrpc.newBlockingStub;



public class ActuatorControllerClient implements ActuatorController {
    private ActuatorControlServiceBlockingStub stub;

    public ActuatorControllerClient(){
        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 50050).usePlaintext().build();
        stub = newBlockingStub(channel);
    }
    @Override
    public void move(ActuatorDirection ad) {
        ActuatorRequest request = ActuatorRequest.newBuilder().setDirection(Direction.forNumber(ad.ordinal())).build();
        stub.move(request);

    }
    
    
}
