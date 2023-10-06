package command;

import controller.Controller;
import logging.Logger;
import model.Booking;
import model.EventType;
import org.junit.jupiter.api.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class BookEventSystemTest {
    static Controller controller = new Controller();

    private static void createUsers() {
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

        //performance1 for event1
        controller.runCommand(new AddEventPerformanceCommand(event1number,
                "Swan Lake Holyrood", LocalDateTime.now().plusMonths(3),
                LocalDateTime.now().plusMonths(3).plusHours(5), Collections.emptyList(),
                true, true, true, 600, 800));

        //performance2 for event1
        controller.runCommand(new AddEventPerformanceCommand(event1number,
                "Odean theatre", LocalDateTime.now().plusMonths(3),
                LocalDateTime.now().plusMonths(3).plusHours(5), Collections.emptyList(),
                true, true, false, 400, 1000));

        CreateTicketedEventCommand eventCmd2 = new CreateTicketedEventCommand("Ballet Bounce",
                EventType.Dance, 500, 30, false);

        controller.runCommand(eventCmd2);
        long event2number = eventCmd2.getResult();

        //performance3 for event2
        controller.runCommand(new AddEventPerformanceCommand(event2number,
                "The balmoral ballroom", LocalDateTime.now().plusMonths(4),
                LocalDateTime.now().plusMonths(4).plusHours(8), Collections.emptyList(),
                true, true, false, 500, 900));

        //performance4 for event2
        controller.runCommand(new AddEventPerformanceCommand(event2number,
                "The balmoral ballroom2", LocalDateTime.now(),
                LocalDateTime.now(), Collections.emptyList(),
                true, true, false, 500, 900));

        //event3
        CreateNonTicketedEventCommand eventCmd3 = new CreateNonTicketedEventCommand("Ballet Bounce2",
                EventType.Dance);

        controller.runCommand(eventCmd3);
        long event3number = eventCmd3.getResult();

        //performance5 for event3
        controller.runCommand(new AddEventPerformanceCommand(event3number,
                "The balmoral ballroom11", LocalDateTime.now().plusMonths(4),
                LocalDateTime.now().plusMonths(4).plusHours(8), Collections.emptyList(),
                true, true, false, 500, 900));

       logout();
    }

    private static void logout() {
        controller.runCommand(new LogoutCommand());
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

    private static List<Booking> getUserBookings() {
        ListConsumerBookingsCommand cmd1 = new ListConsumerBookingsCommand();
        controller.runCommand(cmd1);
        return cmd1.getResult();
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

    private static int getBookingEventTicketsLeft(Booking b) {
        return b.getEventPerformance().getEvent().getOrganiser().getProviderSystem().getNumTicketsLeft(1, 1);
    }

    private static void cancelEvent1() {
        logout();
        loginProvider();
        CancelEventCommand cmd = new CancelEventCommand(1,"ll");
        controller.runCommand(cmd);
        logout();
        loginUser1();
    }

    @BeforeEach
    void printTestName(TestInfo testInfo) {
        System.out.println(testInfo.getDisplayName());
    }

    @BeforeEach
    void setupController() {
        controller = new Controller();
        createUsers();
        createEvents();
    }

    @AfterEach
    void clearController() {
        Logger.getInstance().clearLog();
        System.out.println("---");
        logout();
    }

    @Test
    void verifyInitialStateTest() {
        List<Booking> allBookings = getAllBookings();

        assertEquals(0, allBookings.size(),"All bookings size conform to the setup which contains no booking");

        loginUser1();
        assertEquals(0, getUserBookings().size(),"Number of bookings for a user conform to the setup which contains no booking");
    }

    @Test
    void bookEventInvalidUserTest() {
        //logged out
        BookEventCommand cmd1 = new BookEventCommand(1,1,1);
        controller.runCommand(cmd1);
        assertNull(cmd1.getResult(),"Null bookEvent result for user not logged in");

        //not consumer
        loginProvider();
        BookEventCommand cmd2 = new BookEventCommand(1,1,1);
        controller.runCommand(cmd2);
        assertNull(cmd2.getResult(),"Null bookEvent result for non consumer");
    }

    @Test
    void bookEventValidUserValidBooking() {
        loginUser1();
        //logged in enough tickets
        BookEventCommand cmd1 = new BookEventCommand(1,1,1);
        controller.runCommand(cmd1);
        Long booking1 = cmd1.getResult();

        assertEquals(1, booking1,"BookEvent result conform to the expected BookingNumber");

        //logged in enough tickets sponsored
        sponsorEvent1();
        BookEventCommand cmd2 = new BookEventCommand(1,1,1);
        controller.runCommand(cmd2);
        Long booking2 = cmd2.getResult();

        assertEquals(2, booking2,"BookEvent result conform to the expected BookingNumber");

        List<Booking> bookings = getUserBookings();
        assertEquals(2, bookings.size(),"Number of bookings for a user conform to the expected number after previous bookings");
        assertEquals(20, bookings.get(0).getAmountPaid(),"AmountPaid for a booking conform to the expected value");
        assertEquals(10, bookings.get(1).getAmountPaid(),"AmountPaid for a booking conform to the expected value");
        assertEquals(8, getBookingEventTicketsLeft(bookings.get(0)),"Number of tickets left for a event conform " +
                "to the expected value after previous bookings");

        List<Booking> eventBookings = getEventBookings(1);
        assertEquals(2, eventBookings.size(),"Number of bookings for a event conform to the expected value");
    }

    @Test
    void bookEventInvalidBooking() {
        loginUser1();

        //Performance over
        BookEventCommand cmd1 = new BookEventCommand(2,4,1);
        controller.runCommand(cmd1);
        Long booking = cmd1.getResult();

        assertNull(booking,"Null bookEvent result for performance already over");

        //Cancelled event
        cancelEvent1();

        BookEventCommand cmd2 = new BookEventCommand(1,1,1);
        controller.runCommand(cmd2);
        Long booking2 = cmd2.getResult();

        assertNull(booking2,"Null bookEvent result for cancelled event");

        //Event does not exist
        BookEventCommand cmd3 = new BookEventCommand(5,1,1);
        controller.runCommand(cmd3);
        Long booking3 = cmd2.getResult();

        assertNull(booking3,"Null bookEvent result for non-existing event");

        //Performance does not exist
        BookEventCommand cmd4 = new BookEventCommand(2,10,1);
        controller.runCommand(cmd4);
        Long booking4 = cmd2.getResult();

        assertNull(booking4,"Null bookEvent result for non-existing performance");

        //Not enough tickets
        BookEventCommand cmd5 = new BookEventCommand(2,3,501);
        controller.runCommand(cmd5);
        Long booking5 = cmd5.getResult();

        assertNull(booking5,"Null bookEvent result for not enough tickets for the event");

        //Non-ticketedEvent
        BookEventCommand cmd6 = new BookEventCommand(3,5,1);
        controller.runCommand(cmd6);
        Long booking6 = cmd6.getResult();

        assertNull(booking6,"Null bookEvent result for booking nonTicketedEvent");

        List<Booking> allBookings = getAllBookings();
        assertEquals(0, allBookings.size(),"Number of bookings conform to the expected value after all invalidBookings");
    }

    @Test
    void BookEventInvalidParams() {
        loginUser1();

        // event number 0
        BookEventCommand cmd1 = new BookEventCommand(0,1,1);
        controller.runCommand(cmd1);
        Long booking = cmd1.getResult();

        assertNull(booking,"Null bookEvent result for eventNumber equals 0");

        // event number negative
        cmd1 = new BookEventCommand(-1,1,1);
        controller.runCommand(cmd1);
        booking = cmd1.getResult();

        assertNull(booking,"Null bookEvent result for negative eventNumber");

        // performance number 0
        cmd1 = new BookEventCommand(1,0,1);
        controller.runCommand(cmd1);
        booking = cmd1.getResult();

        assertNull(booking,"Null bookEvent result for performanceNumber equals 0");

        // performance number negative
        cmd1 = new BookEventCommand(1,-1,1);
        controller.runCommand(cmd1);
        booking = cmd1.getResult();

        assertNull(booking,"Null bookEvent result for negative performanceNumber");

        List<Booking> allBookings = getAllBookings();
        assertEquals(0, allBookings.size(),"Number of bookings conform to the expected value after all invalidBookings");
    }
}