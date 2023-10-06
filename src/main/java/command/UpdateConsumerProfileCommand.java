package command;

import controller.Context;
import logging.LogStatus;
import logging.Logger;
import model.Consumer;
import model.ConsumerPreferences;
import model.User;

import java.util.Map;

public class UpdateConsumerProfileCommand extends UpdateProfileCommand {
    private final String oldPassword;
    private final String newName;
    private final String newEmail;
    private final String newPhoneNumber;
    private final String newPassword;
    private final String newPaymentAccountEmail;
    private final ConsumerPreferences newPreferences;

    public UpdateConsumerProfileCommand(String oldPassword, String newName, String newEmail,
                                        String newPhoneNumber, String newPassword,
                                        String newPaymentAccountEmail,
                                        ConsumerPreferences newPreferences) {
        this.oldPassword = oldPassword;
        this.newName = newName;
        this.newEmail = newEmail;
        this.newPhoneNumber = newPhoneNumber;
        this.newPassword = newPassword;
        this.newPaymentAccountEmail = newPaymentAccountEmail;
        this.newPreferences = newPreferences;
        this.successResult = false;
    }

    @Override
    public void execute(Context context) {
        User user = context.getUserState().getCurrentUser();

        // Check profile update is valid
        if (isProfileUpdateInvalid(context, oldPassword, newEmail) || newName == null || newName.isBlank() ||
                newPhoneNumber == null || newPhoneNumber.isBlank() || newPassword == null ||
                newPassword.isBlank() || newPaymentAccountEmail == null || newPaymentAccountEmail.isBlank() ||
                newPreferences == null) {
            Logger.getInstance().logAction("UpdateConsumerProfileCommand.execute()",
                    LogStatus.UpdateConsumerProfileLogStatus.USER_UPDATE_PROFILE_FIELDS_INVALID);
            return;
        }

        // User must be logged in as a consumer
        if (!(user instanceof Consumer)) {
            Logger.getInstance().logAction("UpdateConsumerProfileCommand.execute()",
                    LogStatus.UpdateConsumerProfileLogStatus.USER_UPDATE_PROFILE_NOT_CONSUMER);
            return;
        }

        Consumer consumer = (Consumer) user;

        //Update the consumer's profile
        consumer.setName(newName);
        consumer.setEmail(newEmail);
        consumer.setPhoneNumber(newPhoneNumber);
        consumer.setPaymentAccountEmail(newPaymentAccountEmail);
        consumer.setPreferences(newPreferences);
        consumer.updatePassword(newPassword);

        context.getUserState().addUser(consumer);

        this.successResult = true;
        Logger.getInstance().logAction("UpdateConsumerProfileCommand.execute()",
                LogStatus.UpdateConsumerProfileLogStatus.USER_UPDATE_PROFILE_SUCCESS);
        consumer.notify("Profile update successful");
    }
}