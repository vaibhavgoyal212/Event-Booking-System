package command;

import controller.Context;
import java.util.Map;
import logging.LogStatus;
import logging.Logger;
import model.User;

public class LoginCommand implements ICommand {
    private final String email;
    private final String password;
    private User resultUser;

    public LoginCommand(String email, String password) {
        this.email = email;
        this.password = password;
    }

    @Override
    public void execute(Context context) {
        Map<String, User> allUsers = context.getUserState().getAllUsers();

        // Cannot login when another user is currently logged in
        if (context.getUserState().getCurrentUser() != null) {
            Logger.getInstance().logAction("LoginCommand.execute()",
                    LogStatus.LoginLogStatus.OTHER_USER_ALREADY_LOGIN);
            return;
        }

        // login email must be valid and registered
        if (email == null || !allUsers.containsKey(email) ||
                allUsers.get(email).getEmail() != email) {
            Logger.getInstance().logAction("LoginCommand.execute()",
                    LogStatus.LoginLogStatus.USER_LOGIN_EMAIL_NOT_REGISTERED);
            return;
        }

        User user = allUsers.get(email);
        if (password == null || !user.checkPasswordMatch(password)) {
            Logger.getInstance().logAction("LoginCommand.execute()",
                    LogStatus.LoginLogStatus.USER_LOGIN_WRONG_PASSWORD);
            return;
        }

        resultUser = user;
        context.getUserState().setCurrentUser(user);
        Logger.getInstance().logAction("LoginCommand.execute()",
                LogStatus.LoginLogStatus.USER_LOGIN_SUCCESS);
    }

    @Override
    public User getResult() {
        return resultUser;
    }
}
