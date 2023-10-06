package command;

import controller.Context;
import logging.LogStatus;
import logging.Logger;
import model.User;

public class LogoutCommand implements ICommand {
    @Override
    public void execute(Context context) {
        User user = context.getUserState().getCurrentUser();

        if (user == null) {
            Logger.getInstance().logAction("LogoutCommand.execute()",
                    LogStatus.LogoutLogStatus.USER_LOGOUT_NOT_LOGGED_IN);
        } else {
            context.getUserState().setCurrentUser(null);
            Logger.getInstance().logAction("LogoutCommand.execute()",
                    LogStatus.LogoutLogStatus.USER_LOGOUT_SUCCESS);
        }
    }

    @Override
    public Object getResult() {
        return null;
    }
}