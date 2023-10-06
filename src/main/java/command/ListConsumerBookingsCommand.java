package command;

import controller.Context;
import logging.LogStatus;
import logging.Logger;
import model.Booking;
import model.Consumer;
import model.User;

import java.util.List;

public class ListConsumerBookingsCommand implements ICommand {
    List<Booking> bookings;

    @Override
    public void execute(Context context) {
        User user = context.getUserState().getCurrentUser();

        if (!(user instanceof Consumer)) {
            Logger.getInstance().logAction("ListConsumerBookingsCommand.execute()",
                    LogStatus.General.INVALID_USER);
            return;
        }

        bookings = ((Consumer) user).getBookings();

        Logger.getInstance().logAction("ListConsumerBookingsCommand.execute()",
                LogStatus.ListConsumerBookingsLogStatus.LIST_CONSUMER_BOOKINGS_SUCCESS);
    }

    @Override
    public List<Booking> getResult() {
        return bookings;
    }
}
