package command;

import controller.Controller;
import logging.Logger;
import model.Event;
import model.EventPerformance;
import model.EventType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class CreateEventSystemTest {
    static Controller controller = new Controller();

    private static void createUsers() {
        controller.runCommand(new RegisterEntertainmentProviderCommand(
                "eventProvider1",
                "Narnia",
                "sendmoneyhere@hotmail.com",
                "Def Not ARobot",
                "itsreallyme@gmail.com",
                "supersecretscallops",
                new ArrayList<>(),
                new ArrayList<>()
        ));
        logout();
        controller.runCommand(new RegisterEntertainmentProviderCommand("SlickEntertains",
                "Sombrero Street", "SlickGetsMoney@usurper.com", "Josh Slicker",
                "joshSlickerino@gmail.com", "gotsToGetSlicked",
                new ArrayList<String>(List.of("SlickMan1", "SlickerSool")),
                new ArrayList<String>(List.of("Slick1Shout@gmail.com", "slicksoolshoot@gmail.com"))));
        logout();
    }

    private static void loginEventProvider1() {
        logout();
        controller.runCommand(new LoginCommand("itsreallyme@gmail.com", "supersecretscallops"));
    }

    private static void loginEventProvider2() {
        logout();
        controller.runCommand(new LoginCommand("joshSlickerino@gmail.com", "gotsToGetSlicked"));
    }

    private static void loginGovernment() {
        logout();
        controller.runCommand(new LoginCommand("margaret.thatcher@gov.uk", "The Good times  "));
    }

    private static void logout() {
        controller.runCommand(new LogoutCommand());
    }

    private static List<Event> getAllEvents() {
        logout();
        loginGovernment();
        ListEventsCommand cmd = new ListEventsCommand(false, false);
        controller.runCommand(cmd);
        logout();
        loginEventProvider1();
        return cmd.getResult();
    }

    private static List<Event> getUserEvents() {
        ListEventsCommand cmd = new ListEventsCommand(true, false);
        controller.runCommand(cmd);
        return cmd.getResult();
    }

    private static Long createEvent1() {
        CreateTicketedEventCommand cmd1 = new CreateTicketedEventCommand("a", EventType.Dance,
                5,5,false);
        controller.runCommand(cmd1);
        return cmd1.getResult();
    }

    private static Long createEvent2() {
        CreateNonTicketedEventCommand cmd2 = new CreateNonTicketedEventCommand("b", EventType.Dance);
        controller.runCommand(cmd2);
        return cmd2.getResult();
    }

    private static EventPerformance addP1ToE1() {
        AddEventPerformanceCommand cmd11 = new AddEventPerformanceCommand(1, "a",
                LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2), new ArrayList<>(),
                true, true, true, 5, 5);
        controller.runCommand(cmd11);
        return cmd11.getResult();
    }

    private static EventPerformance addP1ToE2() {
        AddEventPerformanceCommand cmd11 = new AddEventPerformanceCommand(2, "a",
                LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2), new ArrayList<>(),
                true, true, true, 5, 5);
        controller.runCommand(cmd11);
        return cmd11.getResult();
    }

    private static EventPerformance addP0ToE1() {
        AddEventPerformanceCommand cmd11 = new AddEventPerformanceCommand(1, "a",
                LocalDateTime.now().minusDays(1), LocalDateTime.now().minusDays(2),
                new ArrayList<>(),
                true, true, true, 5, 5);
        controller.runCommand(cmd11);
        return cmd11.getResult();
    }

    private static EventPerformance addP2ToE2() {
        AddEventPerformanceCommand cmd21 = new AddEventPerformanceCommand(2, "b",
                LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2), new ArrayList<>(),
                true, true, true, 5, 5);
        controller.runCommand(cmd21);
        return cmd21.getResult();
    }

    private static EventPerformance addP2ToE1() {
        AddEventPerformanceCommand cmd21 = new AddEventPerformanceCommand(1, "b",
                LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2), new ArrayList<>(),
                true, true, true, 5, 5);
        controller.runCommand(cmd21);
        return cmd21.getResult();
    }

    private static void cancelEvent1() {
        logout();
        loginEventProvider1();
        CancelEventCommand cmd = new CancelEventCommand(1,"ll");
        controller.runCommand(cmd);
    }

    @BeforeEach
    void printTestName(TestInfo testInfo) {
        System.out.println(testInfo.getDisplayName());
    }

    @BeforeEach
    void setupController() {
        controller = new Controller();
        createUsers();
    }

    @AfterEach
    void clearLogs() {
        Logger.getInstance().clearLog();
        System.out.println("---");
        logout();
    }

    @Test
    void verifyInitialStateTest() {
        List<Event> allEvents = getAllEvents();

        assertNull(allEvents,"Initial state for all events should be null");

        loginEventProvider1();
        List<Event> allProviderEvents = getUserEvents();
        assertNull(allProviderEvents,"Initial state for user events should be null");
    }

    @Test
    void createEventInvalidUserTest() {
        //logged out
        assertNull(createEvent1(),"Null create event result for user not logged in");

        //not event provider
        loginGovernment();
        assertNull(createEvent1(),"Null create event result for non EntertainmentProvider");

        List<Event> allEvents = getAllEvents();
        assertNull(allEvents,"Null list event result for no successful event creation");
    }

    @Test
    void createValidEventValidUserTest() {
        loginEventProvider1();

        assertEquals(1, createEvent1());
        addP1ToE1();

        assertEquals(2, createEvent2());
        addP2ToE2();

        List<Event> allEvents = getAllEvents();
        assertEquals(2, allEvents.size(),"List event result conform to the number of event creation");

        List<Event> providerEvents = getUserEvents();
        assertEquals(2, providerEvents.size(),"List event result with provider event only conform to " +
                "the number of event created by the provider");
    }

    @Test
    void createEventInvalidEventTest() {
        loginEventProvider1();

        createEvent1();
        addP1ToE1();

        // event already exists
        assertNull(createEvent1(),"Null create event result for creating existing event");
    }

    @Test
    void createTicketedEventInvalidParamsTest() {
        loginEventProvider1();

        // null title
        CreateTicketedEventCommand cmd1 = new CreateTicketedEventCommand(null, EventType.Dance,
                5,5,false);
        controller.runCommand(cmd1);
        assertNull(cmd1.getResult(),"Null create TicketedEvent result for null event title input");

        // null type
        CreateTicketedEventCommand cmd2 = new CreateTicketedEventCommand("a", null,
                5,5,false);
        controller.runCommand(cmd2);
        assertNull(cmd2.getResult(),"Null create TicketedEvent result for null event type input");

        // empty title
        CreateTicketedEventCommand cmd3 = new CreateTicketedEventCommand("  ", EventType.Dance,
                5,5,false);
        controller.runCommand(cmd3);
        assertNull(cmd3.getResult(),"Null create TicketedEvent result for empty event type input");

        // negative price
        CreateTicketedEventCommand cmd4 = new CreateTicketedEventCommand("a", EventType.Dance,
                5,-1,false);
        controller.runCommand(cmd4);
        assertNull(cmd4.getResult(),"Null create TicketedEvent result for negative event ticketPrice input");

        // price 0
        CreateTicketedEventCommand cmd5 = new CreateTicketedEventCommand("a", EventType.Dance,
                5,0,false);
        controller.runCommand(cmd5);
        assertNull(cmd5.getResult(),"Null create TicketedEvent result for event ticketPrice input being 0");

        // negative num tickets
        CreateTicketedEventCommand cmd6 = new CreateTicketedEventCommand("a", EventType.Dance,
                -1,5,false);
        controller.runCommand(cmd6);
        assertNull(cmd6.getResult(),"Null create TicketedEvent result for negative numTickets input");

        // 0 tickets
        CreateTicketedEventCommand cmd7 = new CreateTicketedEventCommand("a", EventType.Dance,
                0,5,false);
        controller.runCommand(cmd7);
        assertNull(cmd7.getResult(),"Null create TicketedEvent result for numTickets input being 0");
    }

    @Test
    void createNonTicketedEventInvalidParamsTest() {
        loginEventProvider1();

        // null title
        CreateNonTicketedEventCommand cmd1 = new CreateNonTicketedEventCommand(null,
                EventType.Dance);
        controller.runCommand(cmd1);
        assertNull(cmd1.getResult(),"Null create NonTicketedEvent result for null event title input");

        // null type
        CreateNonTicketedEventCommand cmd2 = new CreateNonTicketedEventCommand("b", null);
        controller.runCommand(cmd2);
        assertNull(cmd1.getResult(),"Null create NonTicketedEvent result for null event type input");

        // empty title
        CreateNonTicketedEventCommand cmd3 = new CreateNonTicketedEventCommand("  ",
                EventType.Dance);
        controller.runCommand(cmd3);
        assertNull(cmd3.getResult(),"Null create NonTicketedEvent result for empty event title input");
    }

    @Test
    void createPerformanceInvalidUserTest() {
        loginEventProvider1();
        createEvent1();

        // logged out
        logout();
        assertNull(addP1ToE1(),"Null add event performance result for user not logged in");

        // not provider
        loginGovernment();
        assertNull(addP1ToE1(),"Null add event performance result for non EntertainmentProvider");
        logout();

        // not provider of the event
        loginEventProvider2();
        assertNull(addP1ToE1(),"Null add event performance result for non EntertainmentProvider of the event");
    }

    @Test
    void createValidPerformanceValidUserTest() {
        loginEventProvider1();

        Long id1 = createEvent1();
        EventPerformance performance = addP1ToE1();

        assertNotNull(performance,"Not null add event performance result for adding valid performance");
        assertEquals(1, performance.getPerformanceNumber(),"performance number conform to the expected number");
        assertEquals(id1, performance.getEvent().getEventNumber(),"The performance's event number conform to the expected event number");

        Long id2 = createEvent2();
        EventPerformance performance2 = addP2ToE2();

        assertNotNull(performance2,"Not null add event performance result for adding valid performance");
        assertEquals(2, performance2.getPerformanceNumber(),"performance number conform to the expected number");
        assertEquals(id2, performance2.getEvent().getEventNumber(),"The performance's event number conform to the expected event number");

        assertEquals(1, performance.getEvent().getPerformances().size(),"Number of performance in the event conform to the expected number");
        assertEquals(1, performance2.getEvent().getPerformances().size(),"Number of performance in the event conform to the expected number");
    }

    @Test
    void createInvalidPerformance() {
        loginEventProvider1();
        createEvent1();

        // event does not exist
        assertNull(addP2ToE2(),"Null add event performance result for non-existing event");

        createEvent2();
        addP2ToE2();

        // performance in the past
        assertNull(addP0ToE1(),"Null add event performance result for performance's start and end time " +
                "before current time");

        // performance already exists in other event
        addP1ToE1();

        // event cancelled
        cancelEvent1();
        assertNull(addP2ToE1(),"Null add event performance result for already cancelled event");
    }

    @Test
    void createPerformanceInvalidParams() {
        loginEventProvider1();
        createEvent1();

        // negative event number
        AddEventPerformanceCommand cmd1 = new AddEventPerformanceCommand(-1, "a",
                LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2), new ArrayList<>(),
                true, true, true, 5, 5);
        controller.runCommand(cmd1);
        assertNull(cmd1.getResult(),"Null add event performance result for negative event number");

        // event number 0
        AddEventPerformanceCommand cmd2 = new AddEventPerformanceCommand(0, "a",
                LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2), new ArrayList<>(),
                true, true, true, 5, 5);
        controller.runCommand(cmd2);
        assertNull(cmd2.getResult(),"Null add event performance result for event number equals 0");

        // null venue
        AddEventPerformanceCommand cmd3 = new AddEventPerformanceCommand(1, null,
                LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2), new ArrayList<>(),
                true, true, true, 5, 5);
        controller.runCommand(cmd3);
        assertNull(cmd3.getResult(),"Null add event performance result for null venueAddress");

        // empty venue
        AddEventPerformanceCommand cmd4 = new AddEventPerformanceCommand(1, " ",
                LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2), new ArrayList<>(),
                true, true, true, 5, 5);
        controller.runCommand(cmd4);
        assertNull(cmd4.getResult(),"Null add event performance result for empty venueAddress");

        // null start time
        AddEventPerformanceCommand cmd5 = new AddEventPerformanceCommand(1, "a",
                null, LocalDateTime.now().plusDays(2), new ArrayList<>(),
                true, true, true, 5, 5);
        controller.runCommand(cmd5);
        assertNull(cmd5.getResult(),"Null add event performance result for null startDate");

        // null end time
        AddEventPerformanceCommand cmd6 = new AddEventPerformanceCommand(1, "a",
                LocalDateTime.now().plusDays(1), null, new ArrayList<>(),
                true, true, true, 5, 5);
        controller.runCommand(cmd6);
        assertNull(cmd6.getResult(),"Null add event performance result for null endDate");

        // start time after end time
        AddEventPerformanceCommand cmd7 = new AddEventPerformanceCommand(1, "a",
                LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(1).minusHours(1),
                new ArrayList<>(), true, true, true, 5, 5);
        controller.runCommand(cmd7);
        assertNull(cmd7.getResult(),"Null add event performance result for start time after end time");

        // end time in the past
        AddEventPerformanceCommand cmd8 = new AddEventPerformanceCommand(1, "a",
                LocalDateTime.now().plusDays(1), LocalDateTime.now().minusDays(2), new ArrayList<>(),
                true, true, true, 5, 5);
        controller.runCommand(cmd8);
        assertNull(cmd8.getResult(),"Null add event performance result for end time in the past");

        // null performers
        AddEventPerformanceCommand cmd9 = new AddEventPerformanceCommand(1, "a",
                LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2), null,
                true, true, true, 5, 5);
        controller.runCommand(cmd9);
        assertNull(cmd9.getResult(),"Null add event performance result for null performers");

        // negative capacity
        AddEventPerformanceCommand cmd10 = new AddEventPerformanceCommand(1, "a",
                LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2), new ArrayList<>(),
                true, true, true, -1, 5);
        controller.runCommand(cmd10);
        assertNull(cmd10.getResult(),"Null add event performance result for negative capacity");

        // 0 capacity
        AddEventPerformanceCommand cmd11 = new AddEventPerformanceCommand(1, "a",
                LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2), new ArrayList<>(),
                true, true, true, 0, 5);
        controller.runCommand(cmd11);
        assertNull(cmd11.getResult(),"Null add event performance result for negative capacity equals 0");

        //negative venue size
        AddEventPerformanceCommand cmd12 = new AddEventPerformanceCommand(1, "a",
                LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2), new ArrayList<>(),
                true, true, true, 5, -5);
        controller.runCommand(cmd12);
        assertNull(cmd12.getResult(),"Null add event performance result for negative venue size");

        // 0 venue size
        AddEventPerformanceCommand cmd13 = new AddEventPerformanceCommand(1, "a",
                LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2), new ArrayList<>(),
                true, true, true, 5, 0);
        controller.runCommand(cmd13);
        assertNull(cmd13.getResult(),"Null add event performance result for venue size equals 0");
    }
}
