package org.robotcontrol.middleware;

import java.io.IOException;

import io.grpc.BindableService;
import io.grpc.ServerBuilder;

public class Server {
    private io.grpc.Server grpcServer;


    public Server(int port, BindableService... services) {
    ServerBuilder<?> builder = ServerBuilder.forPort(port);
    for (BindableService service : services) {
        builder.addService(service);
    }
    grpcServer = builder.build();
}
    
    /**
     * Starts the server and begins listening for incoming connections.
     * <p>
     * Typically, this should be followed by a blocking call such as {@code awaitTermination()}
     * to keep the application running until explicitly stopped.
     */
    public void Listen() {
        try {
            grpcServer.start();
            System.out.printf("listening on port %d\n", grpcServer.getPort());
        } catch (IOException e) {
            System.out.println(e);
        }
    }

    /**
     * Blocks the current thread and waits for the server to terminate.
     * <p>
     * It is typically called after a non-blocking {@code listen()} or {@code start()} method
     * to prevent the application from exiting prematurely.
     */
    public void awaitTermination() {
        try {
            grpcServer.awaitTermination();
            System.out.println("StateServiceServer exited gracefully");
        } catch (InterruptedException e) {
            System.out.println(e);
        }
    }
}
