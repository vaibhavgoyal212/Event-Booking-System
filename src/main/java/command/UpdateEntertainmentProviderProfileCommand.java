package command;

import controller.Context;
import logging.LogStatus;
import logging.Logger;
import model.Consumer;
import model.EntertainmentProvider;
import model.User;
import state.IUserState;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public class UpdateEntertainmentProviderProfileCommand extends UpdateProfileCommand {
    private final String oldPassword;
    private final String newOrgName;
    private final String newOrgAddress;
    private final String newPaymentAccountEmail;
    private final String newMainRepName;
    private final String newMainRepEmail;
    private final String newPassword;
    private final List<String> newOtherRepNames;
    private final List<String> newOtherRepEmails;

    public UpdateEntertainmentProviderProfileCommand(String oldPassword, String newOrgName,
                                                     String newOrgAddress,
                                                     String newPaymentAccountEmail,
                                                     String newMainRepName, String newMainRepEmail,
                                                     String newPassword, List<String> newOtherRepNames,
                                                     List<String> newOtherRepEmails) {
        this.oldPassword = oldPassword;
        this.newOrgName = newOrgName;
        this.newOrgAddress = newOrgAddress;
        this.newPaymentAccountEmail = newPaymentAccountEmail;
        this.newMainRepName = newMainRepName;
        this.newMainRepEmail = newMainRepEmail;
        this.newPassword = newPassword;
        this.newOtherRepNames = newOtherRepNames;
        this.newOtherRepEmails = newOtherRepEmails;
        this.successResult = false;
    }

    public void execute(Context context) {
        IUserState state = context.getUserState();
        User user = state.getCurrentUser();

        // Check the new profile parameters are all valid
        if (isProfileUpdateInvalid(context, oldPassword, newMainRepEmail) || newOrgAddress == null
                || newOrgAddress.isBlank() || newOrgName == null || newOrgName.isBlank() ||
                newPaymentAccountEmail == null || newPaymentAccountEmail.isBlank() ||
                newMainRepName == null || newMainRepName.isBlank() || newMainRepEmail == null ||
                newMainRepName.isBlank() || newPassword == null || newPassword.isBlank() ||
                newOtherRepEmails == null || newOtherRepNames == null) {
            Logger.getInstance().logAction("UpdateEntertainmentProviderProfileCommand.execute()",
                    LogStatus.UpdateEntertainmentProviderProfileLogStatus.USER_UPDATE_PROFILE_FIELD_INVALID);
            return;
        }

        // User must be logged in as an entertainment provider
        if (!(user instanceof EntertainmentProvider)) {
            Logger.getInstance().logAction("UpdateEntertainmentProviderProfileCommand.execute()",
                    LogStatus.UpdateEntertainmentProviderProfileLogStatus.USER_UPDATE_PROFILE_NOT_PROVIDER);
            return;
        }

        EntertainmentProvider provider = (EntertainmentProvider) user;

        boolean otherOrgRegistered = state.getAllUsers().values().stream().anyMatch(
                (User u) -> (u instanceof EntertainmentProvider) && (!u.equals(provider)) &&
                            ((EntertainmentProvider) u).getOrgName().equals(newOrgName) &&
                            ((EntertainmentProvider) u).getOrgAddress().equals(newOrgAddress));

        if (otherOrgRegistered) {
            Logger.getInstance().logAction("UpdateEntertainmentProviderProfileCommand.execute()",
                    LogStatus.UpdateEntertainmentProviderProfileLogStatus.USER_UPDATE_PROFILE_ORG_ALREADY_REGISTERED);
            return;
        }

        provider.setPaymentAccountEmail(newPaymentAccountEmail);
        provider.setMainRepEmail(newMainRepEmail);
        provider.setMainRepName(newMainRepName);
        provider.setOrgAddress(newOrgAddress);
        provider.setOrgName(newOrgName);
        provider.setOtherRepEmails(newOtherRepEmails);
        provider.setOtherRepNames(newOtherRepNames);
        provider.updatePassword(newPassword);

        context.getUserState().addUser(provider);

        this.successResult = true;
        Logger.getInstance().logAction("UpdateConsumerProfileCommand.execute()",
                LogStatus.UpdateEntertainmentProviderProfileLogStatus.USER_UPDATE_PROFILE_SUCCESS);
    }
}
