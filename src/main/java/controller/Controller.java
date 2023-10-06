package controller;

import command.ICommand;

public class Controller {
    private final Context context;

    public Controller() {
        this.context = new Context();
    }

    public void runCommand(ICommand command) {
        command.execute(this.context);
    }
}
