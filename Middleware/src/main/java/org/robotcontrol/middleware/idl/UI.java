package org.robotcontrol.middleware.idl;

public interface UI {

    // Updated: replaced String[] with bitmap (int robotBitmap) for more efficient
    // representation
    void updateView(String[] robotBitmap, int selected, boolean error, boolean confirm);
}
