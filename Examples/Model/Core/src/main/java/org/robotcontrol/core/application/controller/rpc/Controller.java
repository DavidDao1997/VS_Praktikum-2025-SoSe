package org.robotcontrol.core.application.controller.rpc;
import org.robotcontrol.view.IView;

public class Controller implements IController {

    private final IView view;

    public Controller(IView view) {
        this.view = view;
    }

    @Override
    public void update(String[] robots, int selected, boolean error, boolean confirm) {
        view.updateView(robots, selected, error, confirm);
        System.out.println("UpdateView wurde erfolgreich aufgerufen.");
    }
}


