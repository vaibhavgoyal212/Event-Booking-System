package command;

import controller.Context;
import logging.LogStatus;
import logging.Logger;
import model.User;

import java.util.Map;


public class UpdateProfileCommand implements ICommand {
    protected boolean successResult;

    public UpdateProfileCommand() {
        successResult = false;
    }

    protected boolean isProfileUpdateInvalid(Context context, String oldPassword, String newEmail) {
        User user = context.getUserState().getCurrentUser();

        if (oldPassword == null || newEmail == null || oldPassword.isBlank() || newEmail.isBlank() || user == null) {
            Logger.getInstance().logAction("UpdateProfileCommand.isProfileUpdateInvalid()",
                    LogStatus.UpdateProfileLogStatus.USER_UPDATE_PROFILE_FIELDS_CANNOT_BE_NULL_OR_EMPTY);
            return true;
        }

        if (!user.checkPasswordMatch(oldPassword)) {
            Logger.getInstance().logAction("UpdateProfileCommand.isProfileUpdateValid()",
                    LogStatus.UpdateProfileLogStatus.USER_UPDATE_PROFILE_WRONG_PASSWORD);
            return true;
        }

        Map<String, User> users = context.getUserState().getAllUsers();
        if (users.get(newEmail) != null && !users.get(newEmail).equals(user)) {
            Logger.getInstance().logAction("UpdateProfileCommand.isProfileUpdateValid()",
                    LogStatus.UpdateProfileLogStatus.USER_UPDATE_PROFILE_EMAIL_ALREADY_IN_USE);
            return true;
        }

        return false;
    }

    @Override
    public void execute(Context context) {}

    @Override
    public Boolean getResult() {
        return successResult;
    }
}
