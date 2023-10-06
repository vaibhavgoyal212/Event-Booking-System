package state;

import external.EntertainmentProviderSystem;
import model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

class BookingStateTest {
    BookingState state = new BookingState();

    Consumer newConsumer = new Consumer("newConsumer", "newConsumer@consumer.com",
            "+44123456789", "secure", "consumer@money.com");

    EntertainmentProvider newProvider = new EntertainmentProvider("newProvider",
            "newProviderStreet", "newProvider@money.com",
            "newRep", "newRep@gmail.com", "secure"
            , new ArrayList<>(), new ArrayList<>());

    EntertainmentProvider newProvider2 = new EntertainmentProvider("newProvider2",
            "newProviderStreet2", "newProvider@money.com2",
            "newRep2", "newRep@gmail.com2", "secure2",
            new ArrayList<>(), new ArrayList<>());

    TicketedEvent newTicketedEvent = new TicketedEvent(1, newProvider,
            "newTicketedEvent", EventType.Dance, 10, 1000);

    EventPerformance newEventPerformance = new EventPerformance(1, newTicketedEvent,
            "Mars", LocalDateTime.now().plusHours(1), LocalDateTime.now().plusHours(2),
            Collections.emptyList(), true, true, true,
            500, 1000);

    TicketedEvent newTicketedEvent2 = new TicketedEvent(2, newProvider2, "newTicketedEvent2",
            EventType.Dance, 10, 1000);

    EventPerformance newEventPerformance2 = new EventPerformance(1, newTicketedEvent2,
            "Mars2", LocalDateTime.now().plusHours(12), LocalDateTime.now().plusHours(22),
            Collections.emptyList(), true, true, true,
            500, 1000);

    @BeforeEach
    void resetState() {
        state = new BookingState();

        EntertainmentProviderSystem system = newProvider.getProviderSystem();

        system.recordNewEvent(newTicketedEvent.getEventNumber(), newTicketedEvent.getTitle(),
                newTicketedEvent.getNumTickets());

        system.recordNewPerformance(newTicketedEvent.getEventNumber(),
                newEventPerformance.getPerformanceNumber(), newEventPerformance.getStartDateTime(),
                newEventPerformance.getEndDateTime());

        system.recordNewEvent(newTicketedEvent2.getEventNumber(), newTicketedEvent2.getTitle(),
                newTicketedEvent2.getNumTickets());

        system.recordNewPerformance(newTicketedEvent2.getEventNumber(),
                newEventPerformance2.getPerformanceNumber(),
                newEventPerformance2.getStartDateTime(), newEventPerformance2.getEndDateTime());
    }

    @Test
    void createNewBookingStateTest() {
        BookingState bookingState = new BookingState();

        assertNull(bookingState.findBookingByNumber(1),"No booking can be found at the initial state");
    }

    @Test
    void createNewBookingStateFromOtherTest() {
        BookingState oldBookingState = new BookingState();

        for (int i = 1; i < 10; i++) {
            oldBookingState.createBooking(newConsumer, newEventPerformance, 2, 20);
        }

        state = new BookingState(oldBookingState);
        state.createBooking(newConsumer, newEventPerformance2, 1,1);
        for (int i = 1; i < 10; i++) {
            assertEquals(oldBookingState.findBookingByNumber(i), state.findBookingByNumber(i),"All bookings in old state correspond to new state");
        }
        assertNotNull(state.findBookingByNumber(10),"Booking exists for the last booking number");
        assertNull(state.findBookingByNumber(11),"Null result for finding Booking by non-existing number");
    }

    @Test
    void createValidBookingTest() {
        Booking newBooking = state.createBooking(newConsumer, newEventPerformance, 2, 20);

        assertEquals(1, newBooking.getBookingNumber(),"Booking number correspond to the expected value after creating a valid booking");
    }

    @Test
    void createBookingNullBookerTest() {
        Booking newBooking = state.createBooking(null, newEventPerformance, 2, 20);

        assertNull(newBooking,"CreateBooking returns null for null booker input");
        assertNull(state.findBookingByNumber(1),"No booking is found after invalid booking creation");
    }

    @Test
    void createBookingNullPerformanceTest() {
        Booking newBooking = state.createBooking(newConsumer, null, 2, 20);

        assertNull(newBooking,"CreateBooking returns null for null performance input");
        assertNull(state.findBookingByNumber(1),"No booking is found after invalid booking creation");
    }

    @Test
    void createBookingNegativeTicketsTest() {
        Booking newBooking = state.createBooking(newConsumer, newEventPerformance, -1, 20);

        assertNull(newBooking,"CreateBooking returns null for negative numTickets input");
        assertNull(state.findBookingByNumber(1),"No booking is found after invalid booking creation");
    }

    @Test
    void createBookingNegativeAmountPaidTest() {
        Booking newBooking = state.createBooking(newConsumer, newEventPerformance, 1, -20);

        assertNull(newBooking,"CreateBooking returns null for negative amountPaid input");
        assertNull(state.findBookingByNumber(1),"No booking is found after invalid booking creation");
    }

    @Test
    void findBookingByValidNumberTest() {
        Booking booking = state.createBooking(newConsumer, newEventPerformance, 2, 20);

        assertEquals(booking, state.findBookingByNumber(1),"Booking is found after valid booking creation");
    }

    @Test
    void findBookingByBookingNumber0() {
        state.createBooking(newConsumer, newEventPerformance, 2, 20);

        assertNull(state.findBookingByNumber(0),"No booking is found for booking number equals 0");
    }

    @Test
    void findBookingByBookingNumberPlus1() {
        state.createBooking(newConsumer, newEventPerformance, 2, 20);

        assertNull(state.findBookingByNumber(2),"No booking is found for non-existing booking number");
    }

    @Test
    void findBookingByInNegativeBookingNumber() {
        state.createBooking(newConsumer, newEventPerformance, 2, 20);

        assertNull(state.findBookingByNumber(-1),"No booking is found for negative booking number");
    }

    @Test
    void findBookingsByValidEventNumber() {
        state.createBooking(newConsumer, newEventPerformance, 2, 20);
        state.createBooking(newConsumer, newEventPerformance, 1, 10);
        state.createBooking(newConsumer, newEventPerformance2, 2, 20);

        assertEquals(2, state.findBookingsByEventNumber(1).size(),"Number of bookings found conforms to " +
                "expected value for valid event number");
    }

    @Test
    void findBookingsByNegativeEventNumber() {
        state.createBooking(newConsumer, newEventPerformance, 2, 20);

        assertEquals(0, state.findBookingsByEventNumber(-1).size(),"No booking is found for negative event number");
    }

    @Test
    void findBookingsEventNumber0() {
        state.createBooking(newConsumer, newEventPerformance, 2, 20);

        assertEquals(0, state.findBookingsByEventNumber(0).size(),"No booking is found for event number equals 0");
    }

    @Test
    void findBookingsByEventNumberPlus1() {
        state.createBooking(newConsumer, newEventPerformance, 2, 20);

        assertEquals(0, state.findBookingsByEventNumber(2).size(),"No booking is found for non-existing event number");
    }
}