package command;

import controller.Context;
import java.util.ArrayList;
import java.util.List;
import logging.LogStatus;
import logging.Logger;
import model.GovernmentRepresentative;
import model.SponsorshipRequest;
import model.User;
import state.ISponsorshipState;

public class ListSponsorshipRequestsCommand implements ICommand {
    private final boolean pendingRequestsOnly;
    private List<SponsorshipRequest> outputRequests;

    public ListSponsorshipRequestsCommand(boolean pendingRequestsOnly) {
        this.pendingRequestsOnly = pendingRequestsOnly;
        this.outputRequests = new ArrayList<>();
    }

    @Override
    public void execute(Context context) {
        User user = context.getUserState().getCurrentUser();
        ISponsorshipState state = context.getSponsorshipState();

        // verify current user is a government rep
        if (!(user instanceof GovernmentRepresentative)) {
            Logger.getInstance().logAction("ListSponsorshipRequestsCommand.execute()",
                    LogStatus.General.INVALID_USER);
            return;
        }

        outputRequests = pendingRequestsOnly? state.getPendingSponsorshipRequests():
                                              state.getAllSponsorshipRequests();

        Logger.getInstance().logAction("ListSponsorshipRequestsCommand.execute()",
                LogStatus.ListSponsorshipRequestsLogStatus.SUCCESS);
    }

    @Override
    public List<SponsorshipRequest> getResult() {
        return outputRequests;
    }
}
