package org.robotcontrol.view;


public class ViewMock implements IView {

    @Override
    public void updateView(String[] robots, int selected, boolean error, boolean confirm) {
        System.out.printf("Mock UpdateView: Robots: %s, Selected: %d, Error: %b, Confirm: %b%n",
                          String.join(", ", robots), selected, error, confirm);
    }
}
