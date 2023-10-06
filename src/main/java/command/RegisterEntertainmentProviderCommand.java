package command;

import controller.Context;
import java.util.ArrayList;
import logging.LogStatus;
import logging.Logger;
import model.EntertainmentProvider;
import model.User;
import state.IUserState;

public class RegisterEntertainmentProviderCommand implements ICommand {
    private final String orgName;
    private final String orgAddress;
    private final String paymentAccountEmail;
    private final String mainRepName;
    private final String mainRepEmail;
    private final String password;
    private final ArrayList<String> otherRepNames;
    private final ArrayList<String> otherRepEmails;
    private EntertainmentProvider newEntertainmentProviderResult;

    public RegisterEntertainmentProviderCommand(String orgName, String orgAddress,
                                                String paymentAccountEmail, String mainRepName,
                                                String mainRepEmail, String password,
                                                ArrayList<String> otherRepNames,
                                                ArrayList<String> otherRepEmails) {
        this.orgName = orgName;
        this.orgAddress = orgAddress;
        this.paymentAccountEmail = paymentAccountEmail;
        this.mainRepName = mainRepName;
        this.mainRepEmail = mainRepEmail;
        this.password = password;
        this.otherRepNames = otherRepNames;
        this.otherRepEmails = otherRepEmails;
        this.newEntertainmentProviderResult = null;
    }

    @Override
    public void execute(Context context) {
        IUserState state = context.getUserState();
        //Cannot register entertainment provider if another user is logged in
        if (state.getCurrentUser() != null) {
            Logger.getInstance().logAction("RegisterEntertainmentProviderCommand.execute()",
                    LogStatus.RegisterEntertainmentProviderLogStatus.OTHER_USER_LOGGED_IN);
            return;
        }

        //None of the parameters can be null
        if (orgName == null || orgAddress == null || paymentAccountEmail == null || mainRepName == null ||
            mainRepEmail == null || password == null || otherRepNames == null || otherRepEmails == null) {
            Logger.getInstance().logAction("RegisterEntertainmentProviderCommand.execute()",
                    LogStatus.RegisterEntertainmentProviderLogStatus.USER_REGISTER_FIELDS_CANNOT_BE_NULL);
            return;
        }

        //None of the parameters can be blank
        if (orgName.isBlank() || orgAddress.isBlank() || paymentAccountEmail.isBlank() ||
                mainRepName.isBlank() || mainRepEmail.isBlank() || password.isBlank()) {
            Logger.getInstance().logAction("RegisterEntertainmentProviderCommand.execute()",
                    LogStatus.RegisterEntertainmentProviderLogStatus.USER_REGISTER_FIELDS_CANNOT_BE_BLANK);
            return;
        }

        //See if any organisation is already registered with the same email and address
        boolean orgAlreadyRegistered = state.getAllUsers().values().stream().anyMatch(
                (User u) -> (u.getEmail().equals(mainRepEmail) ||
                            ((u instanceof EntertainmentProvider) && (!u.equals(state.getCurrentUser())) &&
                                ((EntertainmentProvider) u).getOrgAddress().equals(orgAddress))
                 ));

        if (orgAlreadyRegistered) {
            Logger.getInstance().logAction("RegisterEntertainmentProviderCommand.execute()",
                    LogStatus.RegisterEntertainmentProviderLogStatus.USER_REGISTER_ORG_ALREADY_REGISTERED);
            return;
        }

        // Register new entertainment provider
        newEntertainmentProviderResult = new EntertainmentProvider(orgName, orgAddress, paymentAccountEmail, mainRepName,
                mainRepEmail, password, otherRepNames, otherRepEmails);
        context.getUserState().addUser(newEntertainmentProviderResult);

        Logger.getInstance().logAction("RegisterEntertainmentProviderCommand.execute()",
                LogStatus.RegisterEntertainmentProviderLogStatus.REGISTER_ENTERTAINMENT_PROVIDER_SUCCESS);

        //automatically login new entertainment provider
        context.getUserState().setCurrentUser(newEntertainmentProviderResult);
    }

    @Override
    public EntertainmentProvider getResult() {
        return newEntertainmentProviderResult;
    }
}
