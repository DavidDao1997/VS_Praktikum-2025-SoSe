package org.robotcontrol.core.application.stateservice;

import org.robotcontrol.core.application.controller.rpc.Controller;
import org.robotcontrol.core.application.controller.rpc.ControllerClient;

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
