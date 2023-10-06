package command;

import controller.Context;
import java.time.LocalDateTime;
import logging.LogStatus;
import logging.Logger;
import model.*;

public class RespondSponsorshipCommand implements ICommand {
    private final long requestNumber;
    private final int percentToSponsor;
    private boolean successful;

    public RespondSponsorshipCommand(long requestNumber, int percentToSponsor) {
        this.requestNumber = requestNumber;
        this.percentToSponsor = percentToSponsor;
        this.successful = false;
    }

    @Override
    public void execute(Context context) {
        User currentUser = context.getUserState().getCurrentUser();

        // User must be logged in as a government rep
        if (!(currentUser instanceof GovernmentRepresentative)) {
            Logger.getInstance().logAction("RespondSponsorshipCommand.execute()",
                    LogStatus.General.INVALID_USER);
            return;
        }

        // check percentage to sponsor is valid
        if ((percentToSponsor < 0) || (percentToSponsor > 100)) {
            Logger.getInstance().logAction("RespondSponsorshipCommand.execute()",
                    LogStatus.RespondSponsorshipLogStatus.RESPOND_SPONSORSHIP_INVALID_PERCENTAGE);
            return;
        }

        GovernmentRepresentative governmentRep = (GovernmentRepresentative) currentUser;
        SponsorshipRequest request = context.getSponsorshipState().findRequestByNumber(requestNumber);

        //Verify the request exists and has not already been responded to
        if ((request == null) || (request.getStatus() != SponsorshipStatus.PENDING)) {
            Logger.getInstance().logAction("RespondSponsorshipCommand.execute()",
                    LogStatus.RespondSponsorshipLogStatus.RESPOND_SPONSORSHIP_INVALID_REQUEST);
            return;
        }

        TicketedEvent event = request.getEvent();
        EntertainmentProvider provider = event.getOrganiser();

        // check that there exists at least one performance that has not ended
        boolean onePerformanceNotEnded = event.getPerformances().stream().anyMatch(
                (EventPerformance p) -> LocalDateTime.now().isBefore(p.getEndDateTime()));

        if (!onePerformanceNotEnded) {
            Logger.getInstance().logAction("RespondSponsorshipCommand.execute()",
                    LogStatus.RespondSponsorshipLogStatus.RESPOND_SPONSORSHIP_All_PERFORMANCES_ENDED);
            return;
        }

        // Reject Sponsorship
        if (percentToSponsor == 0) {
            provider.getProviderSystem().recordSponsorshipRejection(event.getEventNumber());
            request.reject();

            Logger.getInstance().logAction("RespondSponsorshipCommand.execute()",
                    LogStatus.RespondSponsorshipLogStatus.RESPOND_SPONSORSHIP_REJECT);
            successful = true;
            return;
        }

        //Accept sponsorship
        String govEmail = governmentRep.getPaymentAccountEmail();
        String providerEmail = provider.getPaymentAccountEmail();
        double paymentPrice =
                event.getOriginalTicketPrice() * event.getNumTickets() * percentToSponsor / 100;

        successful = context.getPaymentSystem().processPayment(govEmail, providerEmail, paymentPrice);

        if (successful) {
            Logger.getInstance().logAction("RespondSponsorshipCommand.execute()",
                    LogStatus.RespondSponsorshipLogStatus.RESPOND_SPONSORSHIP_APPROVE_PAYMENT_SUCCESS);

            request.accept(percentToSponsor, govEmail);
            provider.getProviderSystem().recordSponsorshipAcceptance(event.getEventNumber(), percentToSponsor);

        } else {
            Logger.getInstance().logAction("RespondSponsorshipCommand.execute()",
                    LogStatus.RespondSponsorshipLogStatus.RESPOND_SPONSORSHIP_APPROVE_PAYMENT_FAILED);
        }
    }

    @Override
    public Boolean getResult() {
        return successful;
    }
}
