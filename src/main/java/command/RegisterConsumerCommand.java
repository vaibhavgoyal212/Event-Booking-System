package command;

import controller.Context;
import logging.LogStatus;
import logging.Logger;
import model.Consumer;
import model.ConsumerPreferences;
import state.IUserState;

public class RegisterConsumerCommand implements ICommand {
    private final String name;
    private final String email;
    private final String phoneNumber;
    private final String password;
    private final String paymentAccountEmail;
    private Consumer newConsumerResult;

    public RegisterConsumerCommand(String name, String email, String phoneNumber, String password, String paymentAccountEmail) {
        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.password = password;
        this.paymentAccountEmail = paymentAccountEmail;
        this.newConsumerResult = null;
    }

    @Override
    public void execute(Context context) {
        IUserState state = context.getUserState();
        //Cannot register new user when someone else is logged in
        if (state.getCurrentUser() != null) {
            Logger.getInstance().logAction("RegisterConsumerCommand.execute()",
                    LogStatus.RegisterConsumerLogStatus.OTHER_USER_LOGGED_IN);
            return;
        }

        //No input parameters can be null
        if (name == null || email == null || phoneNumber == null || password == null ||
            paymentAccountEmail == null) {
            Logger.getInstance().logAction("RegisterConsumerCommand.execute()",
                    LogStatus.RegisterConsumerLogStatus.USER_REGISTER_FIELDS_CANNOT_BE_NULL);
            return;
        }

        //input parameters cannot be blank
        if (name.isBlank() || email.isBlank() || phoneNumber.isBlank() || password.isBlank() ||
                paymentAccountEmail.isBlank()) {
            Logger.getInstance().logAction("RegisterConsumerCommand.execute()",
                    LogStatus.RegisterConsumerLogStatus.USER_REGISTER_FIELDS_CANNOT_BE_BLANK);
            return;
        }

        //Given email cannot match existing user
        if (state.getAllUsers().get(email) != null) {
            Logger.getInstance().logAction("RegisterConsumerCommand.execute()",
                    LogStatus.RegisterConsumerLogStatus.USER_REGISTER_EMAIL_ALREADY_REGISTERED);
            return;
        }

        //Create new Consumer
        newConsumerResult = new Consumer(name, email, phoneNumber, password, paymentAccountEmail);
        newConsumerResult.setPreferences(new ConsumerPreferences());

        state.addUser(newConsumerResult);

        Logger.getInstance().logAction("RegisterConsumerCommand.execute()",
                LogStatus.RegisterConsumerLogStatus.REGISTER_CONSUMER_SUCCESS);
        newConsumerResult.notify("Registration successful");

        //automatically login the new user
        state.setCurrentUser(newConsumerResult);
    }

    @Override
    public Consumer getResult() {
        return newConsumerResult;
    }
}