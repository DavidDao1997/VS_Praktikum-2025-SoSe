package org.robotcontrol.core.application.stateservice;

import java.util.Scanner;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.robotcontrol.middleware.idl.Controller;
import org.robotcontrol.middleware.idl.StateService.SelectDirection;

public class StateServiceDemo {
    public static void main(String[] args) throws InterruptedException {
        ControllerMock stubController = new ControllerMock();
        StateService svc = new StateService(stubController);

        // Start background health reporting every second
        ScheduledExecutorService healthScheduler = Executors.newSingleThreadScheduledExecutor();
        ScheduledFuture<?> healthTask = healthScheduler.scheduleAtFixedRate(() -> {
            svc.reportHealth("R6A1", "R*");
            svc.reportHealth("R6A2", "R*");
            svc.reportHealth("R6A3", "R*");
            svc.reportHealth("R6A4", "R*");

            svc.reportHealth("R1A1", "R*");
            svc.reportHealth("R1A2", "R*");
            svc.reportHealth("R1A3", "R*");
            svc.reportHealth("R1A4", "R*");

            svc.reportHealth("R2A1", "R*");
            svc.reportHealth("R2A2", "R*");
            svc.reportHealth("R2A3", "R*");
            svc.reportHealth("R2A4", "R*");

            svc.reportHealth("R3A1", "R*");
            svc.reportHealth("R3A2", "R*");
            svc.reportHealth("R3A3", "R*");
            svc.reportHealth("R3A4", "R*");

            svc.reportHealth("R4A1", "R*");
            svc.reportHealth("R4A2", "R*");
            svc.reportHealth("R4A3", "R*");
            svc.reportHealth("R4A4", "R*");

            svc.reportHealth("R5A1", "R*");
            svc.reportHealth("R5A2", "R*");
            svc.reportHealth("R5A3", "R*");
            svc.reportHealth("R5A4", "R*");

        }, 0, 250, TimeUnit.MILLISECONDS);

        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter commands: restart, quit");

        while (true) {
            System.out.print("> ");
            String line = scanner.nextLine();
            if (line == null)
                continue;
            String cmd = line.trim().toLowerCase();
            if ("quit".equals(cmd)) {
                healthTask.cancel(true);
                healthScheduler.shutdownNow();
                System.out.println("Exiting.");

            } else if ("restart".equals(cmd)) {
                System.out.println("Restarting health reports...");
                healthTask.cancel(true);
                healthTask = healthScheduler.scheduleAtFixedRate(() -> {
                    svc.reportHealth("R1A1", "R*");
                    svc.reportHealth("R1A2", "R*");
                    svc.reportHealth("R1A3", "R*");
                    svc.reportHealth("R1A4", "R*");
                }, 0, 1, TimeUnit.SECONDS);
            } else if (cmd.startsWith("select ")) {
                String[] parts = cmd.split("\\s+");
                if (parts.length == 2) {
                    try {
                        SelectDirection dir = SelectDirection.valueOf(parts[1].toUpperCase());
                        svc.select(dir);
                    } catch (IllegalArgumentException e) {
                        System.out.println("Usage: select <UP|DOWN>");
                    }
                } else {
                    System.out.println("Usage: select <UP|DOWN>");
                }
            } else {
                System.out.println("Unknown command: " + line);
            }
        }
        // scanner.close();
    }


    // public static class StateServiceMock implements StateService {
    //     @Override
    //     public void reportHealth(String serviceName, String subscription) {
    //         // TODO Auto-generated method stub
    //         throw new UnsupportedOperationException("Unimplemented method 'reportHealth'");
    //     }

    //     @Override
    //     public void setError(boolean err, boolean confirm) {
    //         logger.info("setError(%s, %s)", err, confirm);
    //     }

    //     @Override
    //     public void select(SelectDirection selectDirection) {
    //         logger.info("select(%s)", selectDirection);
    //     }
    // }

    public static class ControllerMock implements Controller {
        @Override
        public void update(String[] robots, int selected, boolean error, boolean confirm) {
            StringBuilder sb = new StringBuilder();
            for (String robot : robots) {
                sb.append(String.format(" %s", robot));
            }

            System.out.printf("update(%s, %s, %s, %s)\n", sb.toString(), selected, error, confirm);
        }
    }
}