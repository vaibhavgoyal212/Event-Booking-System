package command;

import controller.Context;
import external.EntertainmentProviderSystem;
import external.PaymentSystem;
import java.time.LocalDateTime;
import logging.LogStatus;
import logging.Logger;
import model.*;

public class BookEventCommand implements ICommand {
    private final long eventNumber;
    private final long performanceNumber;
    private final int numTicketsRequested;
    private Booking booking;
    private Long bookingReturn;

    BookEventCommand(long eventNumber, long performanceNumber, int numTicketsRequested) {
        this.eventNumber = eventNumber;
        this.performanceNumber = performanceNumber;
        this.numTicketsRequested = numTicketsRequested;
    }

    @Override
    public void execute(Context context) {
        //Retrieve and verify all booking information is valid
        User user = context.getUserState().getCurrentUser();
        Event event = context.getEventState().findEventByNumber(eventNumber);

        //Ensure user is logged in and a Consumer
        if ((user == null) || !(user instanceof Consumer)) {
            Logger.getInstance().logAction("BookEventCommand.execute()",
                    LogStatus.General.INVALID_USER);
            return;
        }

        Consumer consumer = (Consumer) user;

        //Ensure event with given ID exists, that it is active, and ticketed
        if (event == null) {
            Logger.getInstance().logAction("BookEventCommand.execute()",
                    LogStatus.General.EVENT_NOT_FOUND);
            consumer.notify("Event does not exist");
            return;
        }
        if (!(event instanceof TicketedEvent) || event.getStatus() != EventStatus.ACTIVE) {
            Logger.getInstance().logAction("BookEventCommand.execute()",
                    LogStatus.BookEventLogStatus.BOOK_EVENT_BOOKINGS_NOT_ACCEPTED);
            consumer.notify("This event does not accept bookings.");
            return;
        }

        //Retrieve relevant event and system instances
        TicketedEvent ticketedEvent = (TicketedEvent) event;
        EventPerformance performance = event.getPerformanceByNumber(performanceNumber);
        EntertainmentProviderSystem providerSystem = event.getOrganiser().getProviderSystem();
        PaymentSystem paymentSystem = context.getPaymentSystem();

        //Check that the number of ticket requested is valid, the performance requested exists, and
        //the performance has not ended
        if (numTicketsRequested < 1 || performance == null ||
                LocalDateTime.now().isAfter(performance.getEndDateTime()) ||
                providerSystem.getNumTicketsLeft(eventNumber, performanceNumber) < numTicketsRequested) {

            Logger.getInstance().logAction("BookEventCommand.execute()",
                    LogStatus.BookEventLogStatus.BOOK_EVENT_INVALID_BOOKING_REQUEST);
            consumer.notify("Invalid booking request");
            return;
        }

        // Calculate booking price
        double ticketPrice = ticketedEvent.isSponsored() ? ticketedEvent.getDiscountedTicketPrice() : ticketedEvent.getOriginalTicketPrice();
        double totalPrice = ticketPrice * numTicketsRequested;

        // Try to make payment
        boolean paymentSuccess = paymentSystem.processPayment(consumer.getPaymentAccountEmail(),
                event.getOrganiser().getPaymentAccountEmail(),
                totalPrice);

        this.booking = context.getBookingState().createBooking(consumer, performance, numTicketsRequested, totalPrice);

        // Update booking status and notify provider system and consumer of the booking as necessary
        if (!paymentSuccess) {
            booking.cancelPaymentFailed();

            Logger.getInstance().logAction("BookEventCommand.execute()",
                    LogStatus.BookEventLogStatus.BOOK_EVENT_PAYMENT_FAILED);
            consumer.notify("Booking Payment Failed");
        } else {
            providerSystem.recordNewBooking(eventNumber, performanceNumber, booking.getBookingNumber(), consumer.getName(), user.getEmail(), numTicketsRequested);
            this.bookingReturn = this.booking.getBookingNumber();
            consumer.addBooking(this.booking);

            Logger.getInstance().logAction("BookEventCommand.execute()",
                    LogStatus.BookEventLogStatus.BOOK_EVENT_SUCCESS);
            consumer.notify("Booking Successful! Your new booking id is: " + this.bookingReturn);
        }
    }

    @Override
    public Long getResult() {
        return bookingReturn;
    }
}
