package org.robotcontrol.core.application.controller.rpc;

import org.robotcontrol.middleware.idl.UI;
import org.robotcontrol.middleware.ui.UIClient;
import org.robotcontrol.view.IView;
import org.robotcontrol.view.WebSocketView;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class Controller implements IController {

    private final WebSocketView view;

    public Controller(WebSocketView view) {
        this.view = view;
    }

    @Override
    public void update(String[] robots, int selected, boolean error, boolean confirm) {
        // Konvertiere String[] robots in ein byte[] (anstatt Byte[] verwenden wir
        // byte[])
        byte[] robots2Byte = convertStringArrayToBitmap256(robots);

        UIClient client = new UIClient();
        client.updateView(robots2Byte, selected, error, confirm);
    }

    // Methode zur Konvertierung von String[] in ein 32 Byte langes byte[] für
    // Bitmap256
    private byte[] convertStringArrayToBitmap256(String[] robots) {
        // Berechne die Gesamtgröße des Byte-Arrays, das alle String-Bytes enthält
        StringBuilder combinedRobots = new StringBuilder();

        // Kombiniere alle Roboter-Namen zu einem einzelnen String
        for (String robot : robots) {
            combinedRobots.append(robot);
        }

        // Hole den kombinierten String und konvertiere ihn in ein byte[]
        byte[] byteArray = combinedRobots.toString().getBytes(StandardCharsets.UTF_8);

        // Wenn das Array weniger als 32 Bytes hat, fülle es mit Nullen auf
        if (byteArray.length < 32) {
            byte[] paddedArray = new byte[32];
            System.arraycopy(byteArray, 0, paddedArray, 0, byteArray.length);
            return paddedArray; // Rückgabe des gepolsterten Arrays
        }
        // Wenn das Array mehr als 32 Bytes hat, schneide es auf 32 Bytes zu
        else if (byteArray.length > 32) {
            byte[] truncatedArray = new byte[32];
            System.arraycopy(byteArray, 0, truncatedArray, 0, 32);
            return truncatedArray; // Rückgabe des gekürzten Arrays
        }

        // Wenn das Array genau 32 Bytes hat, gebe es zurück
        return byteArray;
    }
}
