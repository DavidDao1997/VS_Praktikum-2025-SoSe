package org.robotcontrol.udp;

import org.robotcontrol.view.IView;

import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class UdpViewServer implements Runnable {

    private final IView view;
    private final int port;

    public UdpViewServer(IView view, int port) {
        this.view = view;
        this.port = port;
    }

    @Override
    public void run() {
        try (DatagramSocket socket = new DatagramSocket(port)) {
            byte[] buffer = new byte[1024];

            System.out.println("UDP-Server läuft auf Port " + port);

            while (true) {
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet);

                String message = new String(packet.getData(), 0, packet.getLength());
                System.out.println("UDP empfangen: " + message);

                // Beispiel: UDP-Format: "Robo1,Robo2,Robo3;1;false;true"
                String[] parts = message.split(";");
                String[] robots = parts[0].split(",");
                int selected = Integer.parseInt(parts[1]);
                boolean error = Boolean.parseBoolean(parts[2]);
                boolean confirm = Boolean.parseBoolean(parts[3]);

                // updateView aufrufen
                view.updateView(robots, selected, error, confirm);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
