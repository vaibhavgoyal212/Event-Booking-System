package state;

import java.util.List;
import model.Booking;
import model.Consumer;
import model.EventPerformance;


public interface IBookingState {
    Booking createBooking(Consumer booker, EventPerformance performance, int numTickets,
                          double amountPaid);

    Booking findBookingByNumber(long bookingNumber);

    List<Booking> findBookingsByEventNumber(long eventNumber);
}
