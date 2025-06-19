package org.example;

import org.example.rpc.ControllerClient;
import org.example.rpc.Controller;

public class Main {
    public static void main(String[] args) {
//        StateService stateService = new StateService();
//        stateService.register("R1A1");
//        stateService.register("R1A2");
//        stateService.register("R1A3");
//
//        System.out.printf("Available robots: %d\n", stateService.getAvailableRobots().size());
//        stateService.register("R1A4");
//        System.out.printf("Available robots: %d\n", stateService.getAvailableRobots().size());

        Controller controller = new ControllerClient();
        StateService stateService = new StateService(controller);
        stateService.register("R1A1");
        stateService.register("R1A2");
        stateService.register("R1A3");
        System.out.printf("Available robots: %d\n", stateService.getAvailableRobots().size());
        stateService.register("R1A4");
        System.out.printf("Available robots: %d\n", stateService.getAvailableRobots().size());





    }
}
