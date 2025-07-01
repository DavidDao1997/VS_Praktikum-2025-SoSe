package org.robotcontrol;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import org.robotcontrol.WatchDog;
import org.robotcontrol.HealthObserver;

public class WatchdogMain {
    public static void main(String[] args) throws InterruptedException {
        // Scheduler für Timeout-Tasks
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        
        UdpServer server = new UdpServer();

        // WatchDog mit 5s Timeout
        WatchDog watchDog = new WatchDog(5000, scheduler);

        server.addService(45060, new WatchDogServer(watchDog));
        
        server.Listen();
        server.awaitTermination();
        // Scheduler herunterfahren
        scheduler.shutdown();
    }
}
