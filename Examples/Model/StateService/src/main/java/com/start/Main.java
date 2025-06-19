package com.start;

public class Main {
    public static void main(String[] args) {
        StateService stateService = new StateService();
        stateService.register("R1A1");
        stateService.register("R1A2");
        stateService.register("R1A3");
        
        System.out.printf("Available robots: %d\n", stateService.getAvailableRobots().size());
        stateService.register("R1A4");
        System.out.printf("Available robots: %d\n", stateService.getAvailableRobots().size());
    }
}
