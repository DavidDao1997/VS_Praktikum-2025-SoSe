package org.robotcontrol.actuatorcontroller;

import org.robotcontrol.middleware.Server;
import org.robotcontrol.middleware.services.ActuatorControllerServer;

import io.grpc.BindableService;

public class ActuatorControllerMain {
    public static void main(String[] args) {
        // expected order: <stateHost> <statePort> <motorName> <listenHost> <listenPort>
        if (args.length != 5) {
            System.err.println(
                "Usage: ActuatorController <stateHost> <statePort> <motorName> <listenHost> <listenPort>");
            System.exit(1);
        }

        String stateHost   = args[0];
        int    statePort   = Integer.parseInt(args[1]);
        String motorName   = args[2];            // e.g. "R1A1"
        String listenHost  = args[3];            // usually "0.0.0.0" or the Pi‑IP
        int    listenPort  = Integer.parseInt(args[4]);

        ActuatorController ac = new ActuatorController(motorName);

        // StateService‑Client für Register + Heartbeat
        StateServiceClient stateClient = new StateServiceClient(stateHost, statePort);
       
        Server server = new Server(
                            listenPort,
                            (BindableService) new ActuatorControllerServer(ac));
        
        stateClient.register(motorName, listenHost, listenPort);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            ac.shutdown();
            try { stateClient.shutdown(); } catch (InterruptedException ignored) {}
        }));

        server.Listen();
        server.awaitTermination();
    }
    
}
