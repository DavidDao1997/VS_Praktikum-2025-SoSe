package org.robotcontrol.middlewarev2.idl;

public interface UI {

    // Updated: replaced String[] with bitmap (int robotBitmap) for more efficient
    // representation
    void updateView(byte[] robotBitmap, int selected, boolean error, boolean confirm);
}
