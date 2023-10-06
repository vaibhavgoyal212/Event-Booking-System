package state;

import external.EntertainmentProviderSystem;
import model.Booking;
import model.Consumer;
import model.Event;
import model.EventPerformance;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.assertNotNull;

public class BookingState implements IBookingState {
    private final List<Booking> bookings;
    private long nextBookingNumber;

    public BookingState() {
        nextBookingNumber = 1;
        bookings = new ArrayList<>();
    }

    public BookingState(IBookingState other) {
        assertNotNull(other);

        BookingState otherState = (BookingState) other;
        nextBookingNumber = otherState.nextBookingNumber;
        bookings = new ArrayList<>();

        bookings.addAll(otherState.bookings);
    }

    public Booking createBooking(Consumer booker, EventPerformance performance,
                                 int numTickets, double amountPaid) {

        //Verify valid booking input
        if (booker == null || performance == null || numTickets <= 0 || amountPaid <= 0) {
            return null;
        }

        //Create and record new Booking
        Booking newBooking = new Booking(nextBookingNumber, booker, performance, numTickets,
                amountPaid, LocalDateTime.now());

        bookings.add(newBooking);
        nextBookingNumber++;

        return newBooking;
    }

    public Booking findBookingByNumber(long bookingNumber) {
        for (Booking b : bookings) {
            if (b.getBookingNumber() == bookingNumber) {
                return b;
            }
        }
        return null;
    }

    public List<Booking> findBookingsByEventNumber(long eventNumber) {
        return bookings.stream().filter(
                (Booking b) -> b.getEventPerformance().getEvent().getEventNumber() == eventNumber)
                .collect(Collectors.toList());
    }
}
