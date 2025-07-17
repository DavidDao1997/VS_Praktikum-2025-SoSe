package org.robotcontrol.view;

public interface IView {

    void updateView(String[] robots, int selected, boolean error, boolean confirm);

}