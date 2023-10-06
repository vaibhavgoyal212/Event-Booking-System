package command;

import controller.Context;
import logging.LogStatus;
import logging.Logger;
import model.*;

public class CreateTicketedEventCommand extends CreateEventCommand {

    private double ticketPrice;
    private int numTickets;
    private boolean requestSponsorship;

    public CreateTicketedEventCommand(String title, EventType type, int numTickets,
                                      double ticketPrice, boolean requestSponsorship) {
        super(title, type);
        this.ticketPrice = ticketPrice;
        this.numTickets = numTickets;
        this.requestSponsorship = requestSponsorship;
    }

    @Override
    public void execute(Context context) {
        User user = context.getUserState().getCurrentUser();

        // check current user for logged-in and is Entertainment provider
        if (!isUserAllowedToCreateEvent(context)) {
            Logger.getInstance().logAction("CreateTicketedEventCommand.execute()",
                    LogStatus.General.INVALID_USER);
            return;
        }

        TicketedEvent ticketedEvent = context.getEventState().createTicketedEvent((EntertainmentProvider) user, this.title, this.type, this.ticketPrice, this.numTickets);

        // event creation failed
        if (ticketedEvent == null) {
            Logger.getInstance().logAction("CreateTicketedEventCommand.execute()",
                    LogStatus.CreateTicketedEventLogStatus.CREATE_TICKETED_FAILED);
            return;
        }

        // set sponsorship if requested
        if (this.requestSponsorship) {
            SponsorshipRequest sponsorshipRequest = context.getSponsorshipState().addSponsorshipRequest(ticketedEvent);
            ticketedEvent.setSponsorshipRequest(sponsorshipRequest);
            Logger.getInstance().logAction("CreateTicketedEventCommand.execute()",
                    LogStatus.CreateTicketedEventLogStatus.CREATE_EVENT_REQUESTED_SPONSORSHIP);
        }

        this.eventNumberResult = ticketedEvent.getEventNumber();
        Logger.getInstance().logAction("CreateTicketedEventCommand.execute()",
                LogStatus.CreateTicketedEventLogStatus.CREATE_TICKETED_EVENT_SUCCESS);
    }
}
