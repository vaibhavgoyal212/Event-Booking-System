package command;

import controller.Controller;
import logging.Logger;
import model.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

public class CancelBookingSystemTest {
    static Controller controller = new Controller();

    private static void logout(Controller controller) {
        controller.runCommand(new LogoutCommand());
    }

    private static void registerUsers() {
        controller.runCommand(new RegisterConsumerCommand("gajodhar", "gajoocool@lik.com",
                "+447345678123", "gajosinghcool", "gajjopays@pay.com"));
        controller.runCommand(new LogoutCommand());

        controller.runCommand(new RegisterConsumerCommand("jacque", "jackpigeon@dolim.com",
                "+447345643521", "jacquewrites", "jacquemoolah@pay.com"));
        controller.runCommand(new LogoutCommand());

        controller.runCommand(new RegisterEntertainmentProviderCommand("SlickEntertains",
                "Sombrero Street", "SlickGetsMoney@usurper.com", "Josh Slicker",
                "joshSlickerino@gmail.com", "gotsToGetSlicked",
                new ArrayList<String>(List.of("SlickMan1", "SlickerSool")),
                new ArrayList<String>(List.of("Slick1Shout@gmail.com", "slicksoolshoot@gmail.com"))));
        controller.runCommand(new LogoutCommand());
    }

    private static void loginUser1() {
        LoginCommand cmd = new LoginCommand("gajoocool@lik.com", "gajosinghcool");
        controller.runCommand(cmd);
    }

    private static void loginUser2() {
        LoginCommand cmd = new LoginCommand("jackpigeon@dolim.com", "jacquewrites");
        controller.runCommand(cmd);
    }

    private static void loginProvider() {
        LoginCommand cmd = new LoginCommand("joshSlickerino@gmail.com", "gotsToGetSlicked");
        controller.runCommand(cmd);
    }

    private static void createEvents() {
        logout();
        loginProvider();

        CreateTicketedEventCommand eventCmd1 = new CreateTicketedEventCommand("HipHop Hosedown",
                EventType.Dance, 10, 20, true);

        controller.runCommand(eventCmd1);
        long event1number = eventCmd1.getResult();

        //Valid performance for event1

        controller.runCommand(new AddEventPerformanceCommand(event1number,
                "Swan Lake Holyrood", LocalDateTime.now().plusMonths(3),
                LocalDateTime.now().plusMonths(3).plusHours(5), Collections.emptyList(),
                true, true, true, 600, 800));

        //performance for event1
        controller.runCommand(new AddEventPerformanceCommand(event1number,
                "Odean theatre", LocalDateTime.now().plusMonths(3),
                LocalDateTime.now().plusMonths(3).plusHours(5), Collections.emptyList(),
                true, true, false, 400, 1000));

        CreateTicketedEventCommand eventCmd2 = new CreateTicketedEventCommand("Ballet Bounce",
                EventType.Dance, 500, 30, false);

        controller.runCommand(eventCmd2);
        long event2number = eventCmd2.getResult();

        //performance for event2
        controller.runCommand(new AddEventPerformanceCommand(event2number,
                "The balmoral ballroom", LocalDateTime.now().plusMonths(4),
                LocalDateTime.now().plusMonths(4).plusHours(8), Collections.emptyList(),
                true, true, false, 500, 900));

        controller.runCommand(new AddEventPerformanceCommand(event2number,
                "The balmoral ballroom2", LocalDateTime.now(),
                LocalDateTime.now().plusSeconds(5), Collections.emptyList(),
                true, true, false, 500, 900));

        logout();
    }

    private static void logout() {
        controller.runCommand(new LogoutCommand());
    }

    private static void loginGovernment() {
        logout();
        controller.runCommand(new LoginCommand("margaret.thatcher@gov.uk", "The Good times  "));
    }

    private static void sponsorEvent1() {
        loginGovernment();
        RespondSponsorshipCommand cmd = new RespondSponsorshipCommand(1, 50);
        controller.runCommand(cmd);
        logout();
        loginUser1();
    }

    private static void cancelEvent1() {
        logout();
        loginProvider();
        CancelEventCommand cmd = new CancelEventCommand(1,"ll");
        controller.runCommand(cmd);
        logout();
        loginUser1();
    }

    private int getBookingEventTicketsLeft(Booking b) {
        Event event = b.getEventPerformance().getEvent();
        return event.getOrganiser().getProviderSystem().getNumTicketsLeft(event.getEventNumber(),
                b.getEventPerformance().getPerformanceNumber());
    }

    private static List<Booking> getUserBookings() {
        ListConsumerBookingsCommand cmd1 = new ListConsumerBookingsCommand();
        controller.runCommand(cmd1);
        return cmd1.getResult();
    }

    private static List<Booking> getActiveUserBookings() {
        return getUserBookings().stream().filter(
                (Booking b) -> b.getStatus() == BookingStatus.ACTIVE).collect(Collectors.toList());
    }

    private static List<Booking> getAllBookings() {
        loginGovernment();
        List<Booking> bookings = new ArrayList<>();
        ListEventBookingsCommand cmd = new ListEventBookingsCommand(1);
        controller.runCommand(cmd);
        bookings.addAll(cmd.getResult());

        cmd = new ListEventBookingsCommand(2);
        controller.runCommand(cmd);
        bookings.addAll(cmd.getResult());

        logout();
        loginUser1();

        return bookings;
    }

    private static List<Booking> getAllActiveBookings() {
        return getAllBookings().stream().filter(
                (Booking b) -> b.getStatus() == BookingStatus.ACTIVE).collect(Collectors.toList());
    }

    private static List<Booking> getEventBookings(long id) {
        logout();
        loginProvider();
        ListEventBookingsCommand cmd = new ListEventBookingsCommand(id);
        controller.runCommand(cmd);
        logout();
        loginUser1();
        return cmd.getResult();
    }

    private static List<Booking> getActiveEventBookings(long id) {
        return getEventBookings(id).stream().filter(
                (Booking b) -> b.getStatus() == BookingStatus.ACTIVE).collect(Collectors.toList());
    }

    private static void setupBookings() {
        loginUser1();

        controller.runCommand(new BookEventCommand(2,4,1));
        controller.runCommand(new BookEventCommand(1,1,1));
        controller.runCommand(new BookEventCommand(1,2,1));
        controller.runCommand(new BookEventCommand(2,3,1));

        sponsorEvent1();
        controller.runCommand(new BookEventCommand(1,1,1));

        logout();
    }

    @BeforeEach
    void printTestName(TestInfo testInfo) {
        System.out.println(testInfo.getDisplayName());
    }

    @BeforeEach
    void resetController() {
        controller = new Controller();
        registerUsers();
        createEvents();
        setupBookings();
    }

    @AfterEach
    void clearLogs() {
        Logger.getInstance().clearLog();
        System.out.println("---");
        logout();
    }

    @Test
    void verifyInitialStateTest() {
        List<Booking> allBookings = getAllBookings();

        assertEquals(5, allBookings.size(),"All bookings size conform to the setup");

        loginUser1();
        assertEquals(5, getUserBookings().size(),"Size of bookings for a user conform to the setup");
    }

    @Test
    void cancelBookingInvalidUserTest() {
        //logged out
        CancelBookingCommand cmd1 = new CancelBookingCommand(1);
        controller.runCommand(cmd1);
        assertFalse(cmd1.getResult(),"False CancelBooking result for user not logged in");

        //non consumer
        loginProvider();
        CancelBookingCommand cmd2 = new CancelBookingCommand(1);
        controller.runCommand(cmd2);
        assertFalse(cmd2.getResult(),"False CancelBooking result for non consumer");

        logout();

        //consumer cancels someone else's booking
        loginUser2();
        CancelBookingCommand cmd3 = new CancelBookingCommand(1);
        controller.runCommand(cmd3);
        assertFalse(cmd3.getResult(),"False CancelBooking result for consumer cancelling other's booking");
    }

    @Test
    void cancelBookingValidUserValidBooking() {
        loginUser1();

        //logged in valid booking not sponsored
        CancelBookingCommand cmd1 = new CancelBookingCommand(4);
        controller.runCommand(cmd1);
        assertTrue(cmd1.getResult(),"True CancelBooking result for valid consumer " +
                "cancelling a valid not sponsored booking");

        //logged in valid booking sponsored
        CancelBookingCommand cmd2 = new CancelBookingCommand(2);
        controller.runCommand(cmd2);
        assertTrue(cmd2.getResult(),"True CancelBooking result for valid " +
                "consumer cancelling a valid sponsored booking");

        List<Booking> bookings = getActiveUserBookings();
        assertEquals(3, bookings.size(),"Number of bookings conform to the expected number after previous cancellation");
        assertEquals(8, getBookingEventTicketsLeft(bookings.get(1)),"Number of Event Tickets correctly updated after previous cancellation");
        assertEquals(499, getBookingEventTicketsLeft(bookings.get(0)),"Number of Event Tickets correctly updated after previous cancellation ");

        List<Booking> e1Bookings = getActiveEventBookings(1);
        assertEquals(2, e1Bookings.size(),"Number of bookings for a event conform to the expected number after previous cancellation");
        List<Booking> e2Bookings = getActiveEventBookings(2);
        assertEquals(1, e2Bookings.size(),"Number of bookings for a event conform to the expected number after previous cancellation");
    }

    @Test
    void cancelEventInvalidBooking() {
        loginUser1();

        //Performance over
        CancelBookingCommand cmd1 = new CancelBookingCommand(1);
        controller.runCommand(cmd1);
        assertFalse(cmd1.getResult(),"False CancelBooking result for a finished performance");

        //Booking does not exist
        CancelBookingCommand cmd2 = new CancelBookingCommand(10);
        controller.runCommand(cmd2);
        assertFalse(cmd2.getResult(),"False CancelBooking result for a non-existing booking");

        assertEquals(5, getActiveUserBookings().size());

        //Cancelled event
        cancelEvent1();
        CancelBookingCommand cmd3 = new CancelBookingCommand(2);
        controller.runCommand(cmd3);
        assertFalse(cmd3.getResult(),"False CancelBooking result for a cancelled event");

        assertEquals(2, getActiveUserBookings().size(),"Number of bookings for a user conform to the " +
                "expected number after previous cancellation");

        //Booking cancelled already by consumer
        CancelBookingCommand cmd4 = new CancelBookingCommand(4);
        controller.runCommand(cmd4);

        CancelBookingCommand cmd5 = new CancelBookingCommand(4);
        controller.runCommand(cmd5);
        assertFalse(cmd5.getResult(),"False CancelBooking result for booking previously cancelled");

        assertEquals(1, getActiveUserBookings().size(),"Number of bookings for a user conform to the " +
                "expected number after previous cancellation");
    }

    @Test
    void cancelBookingInvalidParams() {
        loginUser1();

        // booking number 0
        CancelBookingCommand cmd1 = new CancelBookingCommand(0);
        controller.runCommand(cmd1);
        assertFalse(cmd1.getResult(),"False CancelBooking result for booking number equals 0");

        // booking number negative
        CancelBookingCommand cmd2 = new CancelBookingCommand(-1);
        controller.runCommand(cmd2);
        assertFalse(cmd2.getResult(),"False CancelBooking result for negative booking number");

        assertEquals(5, getActiveUserBookings().size(),"Number of bookings for a user conform to the " +
                "expected number after previous cancellation");
    }
}
