package command;

import controller.Context;

public interface ICommand {
    void execute(Context context);

    Object getResult();
}
