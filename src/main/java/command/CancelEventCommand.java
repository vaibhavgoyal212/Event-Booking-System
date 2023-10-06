package command;

import controller.Context;
import java.time.LocalDateTime;
import java.util.*;
import logging.LogStatus;
import logging.Logger;
import model.*;

public class CancelEventCommand implements ICommand {
    private final long eventNumber;
    private final String organiserMessage;
    private boolean result;

    public CancelEventCommand(long eventNumber, String organiserMessage) {
        this.eventNumber = eventNumber;
        this.organiserMessage = organiserMessage;
        this.result = false;
    }

    @Override
    public void execute(Context context) {
        // user and event are correct
        User user = context.getUserState().getCurrentUser();
        Event event = context.getEventState().findEventByNumber(eventNumber);

        if (!userIsAllowedToCancelEvent(context, event)) {
            return;
        }

        EntertainmentProvider provider = (EntertainmentProvider) user;

        //Refund consumers and government if event is ticketed (and sponsored)
        if (event instanceof TicketedEvent) {
            TicketedEvent ticketedEvent = (TicketedEvent) event;
            double originalTicketPrice = ticketedEvent.getOriginalTicketPrice();
            double discountTicketPrice = ticketedEvent.getDiscountedTicketPrice();

            // refund government if event has been sponsored
            if (ticketedEvent.isSponsored()) {
                double sponsoredAmount = (originalTicketPrice - discountTicketPrice) * ticketedEvent.getNumTickets();
                boolean govRefundSuccessful = context.getPaymentSystem().processRefund(
                        ticketedEvent.getSponsorAccountEmail(),
                        provider.getPaymentAccountEmail(),
                        sponsoredAmount);

                if (govRefundSuccessful) {
                    Logger.getInstance().logAction("CancelEventCommand.execute()",
                            LogStatus.CancelEventLogStatus.CANCEL_EVENT_REFUND_SPONSORSHIP_SUCCESS);
                } else {
                    Logger.getInstance().logAction("CancelEventCommand.execute()",
                            LogStatus.CancelEventLogStatus.CANCEL_EVENT_REFUND_SPONSORSHIP_FAILED);
                    return;
                }
            }

            //refund all active bookings across all performances
            List<Booking> eventBookings = context.getBookingState().findBookingsByEventNumber(eventNumber);
            boolean bookingRefundSuccessful;
            Consumer booker;
            for (Booking booking : eventBookings) {
                booker = booking.getBooker();

                if (booking.getStatus() != BookingStatus.ACTIVE) {
                    continue;
                }

                booker.notify(organiserMessage);
                bookingRefundSuccessful = context.getPaymentSystem().processRefund(
                        booker.getPaymentAccountEmail(),
                        provider.getPaymentAccountEmail(),
                        booking.getAmountPaid());

                if (bookingRefundSuccessful) {
                    booking.cancelByProvider();

                    booker.notify("Booking Refund Successful");
                    Logger.getInstance().logAction("CancelEventCommand.execute()",
                            LogStatus.CancelEventLogStatus.BOOKING_REFUND_SUCCESS);
                } else {
                    booking.getBooker().notify("Booking Refund Unsuccessful, contact " +
                            "organiser");
                    Logger.getInstance().logAction("CancelEventCommand.execute()",
                            LogStatus.CancelEventLogStatus.BOOKING_REFUND_FAILED);
                }
            }
        }//end of ticket refunds

        // update provider ad eventState systems
        provider.getProviderSystem().cancelEvent(eventNumber, organiserMessage);
        event.cancel();

        result = true;
        Logger.getInstance().logAction("CancelEventCommand.execute()",
                LogStatus.CancelEventLogStatus.CANCELLATION_COMPLETE);
    }

    private boolean userIsAllowedToCancelEvent(Context context, Event event) {
        User user = context.getUserState().getCurrentUser();

        if (event == null) {
            Logger.getInstance().logAction("CancelEventCommand.execute()",
                    LogStatus.CancelEventLogStatus.CANCEL_EVENT_EVENT_NOT_FOUND);
            return false;
        }

        //make sure user is the event organiser
        if (user == null || !user.equals(event.getOrganiser())) {
            Logger.getInstance().logAction("CancelEventCommand.execute()",
                    LogStatus.General.INVALID_USER);
            return false;
        }

        Collection<EventPerformance> performances = event.getPerformances();

        //Verify event is still active and the organiser message is not invalid
        if (event.getStatus() != EventStatus.ACTIVE || organiserMessage == null ||
                organiserMessage.isBlank()) {
            Logger.getInstance().logAction("CancelEventCommand.execute()",
                    LogStatus.CancelEventLogStatus.CANCELLATION_INVALID);
            return false;
        }

        //Verify that no performances have already started
        for (EventPerformance performance : performances) {
            if (LocalDateTime.now().isAfter(performance.getEndDateTime()) ||
                    LocalDateTime.now().isAfter(performance.getStartDateTime())) {
                Logger.getInstance().logAction("CancelEventCommand.execute()",
                        LogStatus.CancelEventLogStatus.CANCEL_EVENT_FAILED_PERFORMANCE_ALREADY_STARTED);
                return false;
            }
        }

        return true;
    }

    @Override
    public Boolean getResult() {
        return result;
    }
}
