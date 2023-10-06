package command;

import controller.Context;
import java.util.List;
import logging.LogStatus;
import logging.Logger;
import model.*;

public class ListEventBookingsCommand implements ICommand {
    private final long eventNumber;
    private List<Booking> bookingListResult;

    public ListEventBookingsCommand(long eventNumber) {
        this.eventNumber = eventNumber;
    }

    @Override
    public void execute(Context context) {
        User currentUser = context.getUserState().getCurrentUser();
        Event event = context.getEventState().findEventByNumber(eventNumber);

        if (!(event instanceof TicketedEvent)) {
            Logger.getInstance().logAction("ListEventBookingsCommand.execute()",
                    LogStatus.ListEventBookingsLogStatus.LIST_EVENT_BOOKINGS_EVENT_NOT_TICKETED);
            return;
        }

        if (!(currentUser instanceof GovernmentRepresentative) &&
            !(currentUser.equals(event.getOrganiser()))) {
            Logger.getInstance().logAction("ListEventBookingsCommand.execute()",
                    LogStatus.General.INVALID_USER);
            return;
        }

        bookingListResult = context.getBookingState().findBookingsByEventNumber(eventNumber);

        Logger.getInstance().logAction("ListEventBookingsCommand.execute()",
                LogStatus.ListEventBookingsLogStatus.LIST_EVENT_BOOKINGS_SUCCESS);
    }

    @Override
    public List<Booking> getResult() {
        return bookingListResult;
    }
}
