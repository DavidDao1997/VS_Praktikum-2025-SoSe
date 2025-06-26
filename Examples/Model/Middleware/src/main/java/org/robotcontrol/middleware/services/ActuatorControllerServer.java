
package org.robotcontrol.middleware.services;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.robotcontrol.middleware.RpcValue;
import org.robotcontrol.middleware.ServerStub;

import actuatorController.Actuator.ActuatorRequest;
import actuatorController.Actuator.Empty;
import actuatorController.ActuatorControlServiceGrpc.ActuatorControlServiceImplBase;

public class ActuatorControllerServer extends ActuatorControlServiceImplBase {

    private ServerStub actuatorController;
    private Map<String, Method> methods;

    public ActuatorControllerServer(ServerStub actuatorController){
        this.actuatorController = actuatorController;
        methods = new HashMap<>();
        // populate methods map by reflection from target class
        for (Method m : actuatorController.getClass().getMethods()) {
            methods.put(m.getName(), m);
        }
    }
    
    @Override
    public void move(ActuatorRequest request,
        io.grpc.stub.StreamObserver<Empty> responseObserver) {
            System.out.printf("Move was called\n");
            //actuatorController.call("move", new RpcValue.IntValue(request.getDirectionValue()));
            responseObserver.onNext(Empty.getDefaultInstance());
            responseObserver.onCompleted();
        }
}
