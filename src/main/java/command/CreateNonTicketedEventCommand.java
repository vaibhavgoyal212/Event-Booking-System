package command;

import controller.Context;
import logging.LogStatus;
import logging.Logger;
import model.EntertainmentProvider;
import model.EventType;
import model.NonTicketedEvent;
import model.User;

public class CreateNonTicketedEventCommand extends CreateEventCommand {
    public CreateNonTicketedEventCommand(String title, EventType type) {
        super(title, type);
    }

    @Override
    public void execute(Context context) {
        User user = context.getUserState().getCurrentUser();

        // check for logged-in and is an Entertainment provider
        if (!isUserAllowedToCreateEvent(context)) {
            Logger.getInstance().logAction("CreateNonTicketedEventCommand.execute()",
                    LogStatus.General.INVALID_USER);
            return;
        }

        EntertainmentProvider provider = (EntertainmentProvider) user;

        // create event
        NonTicketedEvent nonTicketedEvent = context.getEventState().createNonTicketedEvent(provider,
                this.title, this.type);

        if (nonTicketedEvent == null) {
            Logger.getInstance().logAction("CreateNonTicketedEventCommand.execute()",
                    LogStatus.CreateNonTicketedEventLogStatus.CREATE_NON_TICKETED_FAILED);
            return;
        }

        this.eventNumberResult = nonTicketedEvent.getEventNumber();
        Logger.getInstance().logAction("CreateNonTicketedEventCommand.execute()",
                LogStatus.CreateNonTicketedEventLogStatus.CREATE_NON_TICKETED_EVENT_SUCCESS);
    }
}
