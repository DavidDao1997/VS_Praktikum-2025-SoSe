

package org.robotcontrol.udp;

import java.net.DatagramSocket;
import java.net.DatagramPacket;
import org.robotcontrol.WatchDog;

/**
 * UDP-Server für WatchDog-CheckIns.
 */
public class UdpWatchdogServer implements Runnable {

    private final WatchDog watchDog;
    private final int port;

    public UdpWatchdogServer(WatchDog watchDog, int port) {
        this.watchDog = watchDog;
        this.port = port;
    }

  @Override
public void run() {
    try (DatagramSocket socket = new DatagramSocket(port)) {
        byte[] buffer = new byte[1024];
        System.out.println("WatchDog-UDP-Server läuft auf Port " + port);
        while (true) {
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
            socket.receive(packet);

            // Nachricht parsen: Format "COMMAND:argument"
            String message = new String(packet.getData(), 0, packet.getLength()).trim();
            System.out.println("WatchDog UDP empfangen: " + message);

            String[] parts = message.split(":", 2);
            if (parts.length < 2) {
                System.err.println("Ungültiges Format, erwartet COMMAND:argument");
                continue;
            }

            String command = parts[0].trim().toLowerCase();
            String argument = parts[1].trim();

            switch (command) {
                case "checkin":
                    watchDog.checkIn(argument);
                    break;
                case "subscribe":
                    watchDog.subscribe(argument);
                    break;
                default:
                    System.err.println("Unbekannter Befehl: " + command);
            }
        }
    } catch (Exception e) {
        System.err.println("Fehler im UdpWatchdogServer: " + e.getMessage());
        e.printStackTrace();
    }
}
}