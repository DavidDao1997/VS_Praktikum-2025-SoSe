// ➜ src/main/java/…/ActuatorControlServiceImpl.java

import io.grpc.stub.StreamObserver;
import vs.grpc.ActuatorControlServiceGrpc;
import vs.grpc.ActuatorRequest;
import vs.grpc.Direction;
import vs.grpc.MoveResponse;                // ← new import

public class ActuatorControlServiceImpl
        extends ActuatorControlServiceGrpc.ActuatorControlServiceImplBase {

    private final ActuatorController controller;

    public ActuatorControlServiceImpl(String host, int port, String actuator) throws Exception {
        this.controller = new ActuatorController(host, port, actuator);
    }

    @Override
    public void move(ActuatorRequest request,
                     StreamObserver<MoveResponse> responseObserver) {

        Direction dir = request.getDirection();
        ActuatorController.Direction hwDir =
                (dir == Direction.INCREASE)
                        ? ActuatorController.Direction.INCREASE
                        : ActuatorController.Direction.DECREASE;

        try {
            controller.move(hwDir);

            MoveResponse ok = MoveResponse.newBuilder()
                    .setOk(true)
                    .build();
            responseObserver.onNext(ok);
            responseObserver.onCompleted();

        } catch (Exception ex) {
            MoveResponse fail = MoveResponse.newBuilder()
                    .setOk(false)
                    .setErrorMsg(ex.getMessage())
                    .build();
            responseObserver.onNext(fail);
            responseObserver.onCompleted();
        }
    }
}