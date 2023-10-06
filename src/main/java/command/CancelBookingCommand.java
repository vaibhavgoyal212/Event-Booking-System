package command;

import controller.Context;
import java.time.LocalDateTime;
import logging.LogStatus;
import logging.Logger;
import model.*;

public class CancelBookingCommand implements ICommand {
    private final long bookingNumber;
    private boolean confirmation;

    public CancelBookingCommand(long bookingNumber) {
        this.bookingNumber = bookingNumber;
        this.confirmation = false;
    }

    @Override
    public void execute(Context context) {
        //Verify user and booking are valid
        User user = context.getUserState().getCurrentUser();
        Booking booking = context.getBookingState().findBookingByNumber(bookingNumber);

        if (!(user instanceof Consumer)) {
            Logger.getInstance().logAction("CancelBookingCommand.execute()",
                    LogStatus.General.INVALID_USER);
            return;
        }

        Consumer consumer = (Consumer) user;

        if (booking == null) {
            Logger.getInstance().logAction("CancelBookingCommand.execute()",
                    LogStatus.CancelBookingLogStatus.CANCEL_BOOKING_BOOKING_NOT_FOUND);
            consumer.notify("Booking does not exist");
            return;
        }

        // verify consumer is owner of the booking, booking is active and not starting within 24hrs
        if (consumer != (booking.getBooker()) || booking.getStatus() != BookingStatus.ACTIVE ||
            LocalDateTime.now().isAfter(booking.getEventPerformance().getStartDateTime().minusHours(24))) {

            Logger.getInstance().logAction("CancelBookingCommand.execute()",
                    LogStatus.CancelBookingLogStatus.CANCEL_BOOKING_INVALID_BOOKING_REQUEST);
            consumer.notify("You are not permitted to cancel this booking");
            return;
        }

        // process refund
        String bookerEmail = consumer.getPaymentAccountEmail();
        EntertainmentProvider provider = booking.getEventPerformance().getEvent().getOrganiser();
        String providerEmail = provider.getPaymentAccountEmail();
        boolean refundSuccessful = context.getPaymentSystem().processRefund(bookerEmail, providerEmail,
                                                                            booking.getAmountPaid());
        if (!refundSuccessful) {
            Logger.getInstance().logAction("CancelBookingCommand.execute()",
                    LogStatus.CancelBookingLogStatus.CANCEL_BOOKING_REFUND_FAILED);
            consumer.notify("Refund failed, cancellation unsuccessful");
            return;
        }

        // booking successful, update booking records
        this.confirmation = true;
        booking.cancelByConsumer();
        provider.getProviderSystem().cancelBooking(bookingNumber);

        Logger.getInstance().logAction("CancelBookingCommand.execute()",
                LogStatus.CancelBookingLogStatus.CANCEL_BOOKING_SUCCESS);
        consumer.notify("Booking cancellation successful");
    }

    @Override
    public Boolean getResult() {
        return confirmation;
    }
}
