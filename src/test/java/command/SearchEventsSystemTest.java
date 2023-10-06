package command;

import controller.Controller;
import logging.Logger;
import model.ConsumerPreferences;
import model.Event;
import model.EventType;
import org.junit.jupiter.api.*;

import javax.print.DocFlavor;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class SearchEventsSystemTest {
    private static Controller controller = new Controller();

    private static void createUsers() {
        controller.runCommand(new RegisterConsumerCommand("kanta", "kantabai@gmail.com",
                "+447334679078", "kantakichaat", "kantakikharchi@pay.com"));
        updateKantaPref();
        controller.runCommand(new LogoutCommand());
    }

    private static void loginKanta() {
        LoginCommand cmd = new LoginCommand("kantabai@gmail.com", "kantakichaat");
        controller.runCommand(cmd);
    }

    private static void updateKantaPref() {
        ConsumerPreferences preferences = new ConsumerPreferences();
        preferences.preferSocialDistancing = false;
        preferences.preferAirFiltration = true;
        preferences.preferOutdoorsOnly = true;
        preferences.preferredMaxCapacity = 400;
        preferences.preferredMaxVenueSize = 700;

        controller.runCommand(new UpdateConsumerProfileCommand(
                "kantakichaat", "kanta", "kantabai@gmail.com", "+447334679078",
                "kantakichaat", "kantakikharchi@pay.com", preferences
        ));
    }

    private static void loginProvider1() {
        LoginCommand cmd = new LoginCommand("MarkStonieEats@gmail.com", "SirMarksEatALot");
        controller.runCommand(cmd);
    }

    private static void loginProvider2() {
        LoginCommand cmd = new LoginCommand("busk@every.day", "hey");
        controller.runCommand(cmd);
    }

    private static void createProvider1() {
        controller.runCommand(new RegisterEntertainmentProviderCommand("DanceMania",
                "St James Quarters, Edinburgh", "weneedmoney@gmail.com",
                "Mark Stonie", "MarkStonieEats@gmail.com", "SirMarksEatALot",
                new ArrayList<>(List.of("Jack Pog")), new ArrayList<>(List.of("jackpoggers@gmail.com"))));

        CreateTicketedEventCommand eventCmd1 = new CreateTicketedEventCommand("HipHop Hosedown",
                EventType.Dance, 1000, 15, true);


        //Event 1: Has 1 performance that matches Kanta's requirements, has a performance 1 day
        // from now and another one 2 days from now that does not match their profile
        controller.runCommand(eventCmd1);
        long event1number = eventCmd1.getResult();

        controller.runCommand(new AddEventPerformanceCommand(event1number, "Swan Lake Holyrood",
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(1).plusHours(5), Collections.emptyList(),
                true, true, true, 300, 600));

        controller.runCommand(new AddEventPerformanceCommand(event1number, "Odean theatre",
                LocalDateTime.now().plusDays(2),
                LocalDateTime.now().plusDays(2).plusHours(5), Collections.emptyList(),
                true, true, false, 600, 1000));

        //Event 2: Has one performance that match Kanta's requirements but that has already
        // finished, has another performance 1 day from now that does not match Kanta's
        // requirements
        CreateTicketedEventCommand eventCmd2 = new CreateTicketedEventCommand("Ballet Bounce",
                EventType.Dance, 500, 30, false);

        controller.runCommand(eventCmd2);
        long event2number = eventCmd2.getResult();

        //performances for event2
        controller.runCommand(new AddEventPerformanceCommand(event2number, "The balmoral ballroom",
                LocalDateTime.now(), LocalDateTime.now(), Collections.emptyList(),
                false, true, true, 200, 600));

        controller.runCommand(new AddEventPerformanceCommand(event2number, "The balmoral ballroom2",
                LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(1).plusHours(1),
                Collections.emptyList(),
                true, true, true, 500, 900));

        controller.runCommand(new LogoutCommand());
    }

    private static void createProvider2() {
        controller.runCommand(new RegisterEntertainmentProviderCommand(
                "BuskersOrg",
                "Leith Walk",
                "buskergetsmoney@gmail.com",
                "the best musicican ever",
                "busk@every.day",
                "hey",
                new ArrayList<>(),
                new ArrayList<>()
        ));

        //Event 1: An event with one performance in the past, that matches Kanta's preferences
        CreateTicketedEventCommand eventCmd = new CreateTicketedEventCommand(
                "Music for everyone!",
                EventType.Music, 200, 30, false);
        controller.runCommand(eventCmd);
        long eventNumber = eventCmd.getResult();

        controller.runCommand(new AddEventPerformanceCommand(
                eventNumber,
                "Leith as usual",
                LocalDateTime.now(),
                LocalDateTime.now(),
                List.of("The same musician"),
                true,
                true,
                true,
                100,
                100
        ));

        //Event2: An event with one performance two days from now that matches Kanta's profile,
        // another event one day from now that does not match Kanta's profile
        CreateNonTicketedEventCommand event2cmd = new CreateNonTicketedEventCommand("singyourheartout", EventType.Music);
        controller.runCommand(event2cmd);
        long event2num = event2cmd.getResult();

        controller.runCommand(new AddEventPerformanceCommand(
                event2num, "playhouse theatre",
                LocalDateTime.now().plusDays(2),
                LocalDateTime.now().plusDays(2).plusHours(1),
                List.of("Serendipity"),
                true,
                true,
                true, 200, 400
        ));

        controller.runCommand(new AddEventPerformanceCommand(
                event2num, "playhouse theatre",
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(1).plusHours(1),
                List.of("Serendipity"),
                true,
                false,
                true, 200, 400
        ));

        controller.runCommand(new LogoutCommand());
    }

    private static void cancelEvent1() {
        logout();
        loginProvider1();
        controller.runCommand(new CancelEventCommand(1, "heyo"));
        logout();
    }

    private static List<Event> listEventsOnGivenDate(boolean userOnly, boolean activeOnly,
                                                        LocalDateTime searchDateTime) {
        ListEventsOnGivenDateCommand listEventsCmd = new ListEventsOnGivenDateCommand(userOnly, activeOnly, searchDateTime);
        controller.runCommand(listEventsCmd);
        return listEventsCmd.getResult();
    }

    private static void loginGovernment() {
        controller.runCommand(new LoginCommand("margaret.thatcher@gov.uk", "The Good times  "));
    }

    private static List<Event> listEvents(boolean userEvents, boolean activeEvents) {
        ListEventsCommand cmd = new ListEventsCommand(userEvents, activeEvents);
        controller.runCommand(cmd);
        return cmd.getResult();
    }

    private static void logout(){
        controller.runCommand(new LogoutCommand());
    }

    @BeforeEach
    void printTestName(TestInfo testInfo) {
        System.out.println(testInfo.getDisplayName());
    }

    @BeforeEach
    void setupState() throws InterruptedException {
        controller = new Controller();
        createUsers();
        createProvider1();
        createProvider2();

        // this creates:
        //events that do and don't match Kanta's profile
        //events where all events are active (and can be cancelled), possible in the past
        //event performances are on different days
    }

    @AfterEach
    void clearLogs() {
        Logger.getInstance().clearLog();
        System.out.println("---");
    }

    @Test
    void searchEventsInvalidUserTest() {
        //logged out userEventsOnly
        assertNull(listEvents(true, false),"Null list user only events result for user not logged in");
        assertNull(listEventsOnGivenDate(true, false, LocalDateTime.now()),"Null listEventsOnGivenDate " +
                "user events only result for user not logged in");

        //Government userEventsOnly
        loginGovernment();
        assertNull(listEvents(true, false),"Null list user only events result for Government representative logged in");
        assertNull(listEventsOnGivenDate(true, false, LocalDateTime.now()),"Null listEventsOnGivenDate " +
                "user events only result for Government representative logged in");
    }

    @Test
    void searchEventsNoDateSuccessTest() {
        // logged out search for all events
        // all events but event 3 (whose only performance has finished) should be found
        List<Event> result = listEvents(false, false);
        assertEquals(3, result.size(),"Number of listed events conform to the expected value when no user logged in");
        assertTrue(result.stream().noneMatch(
                (Event e) -> e.getEventNumber() == 3),"Event that are invalid for listing are not listed");

        // provider events
        // provider 1 has two events, both have active performances
        loginProvider1();
        result = listEvents(true, false);
        assertEquals(2, result.size(),"Number of user only events listed conform to the expected value for entertainment provider logged in");
        logout();

        //provider 2 has two events but one of them has no more active performances
        loginProvider2();
        result = listEvents(true, false);
        assertEquals(1, result.size(),"Number of user only events listed conform to the expected value for entertainment provider logged in");
        logout();

        // consumer events
        // One event from each provider matches Kanta's requirements and has not finished
        loginKanta();
        result = listEvents(true, false);
        assertEquals(2, result.size(),"Number of user only events listed conform to the expected value for consumer logged in");
        assertEquals(1, result.get(0).getEventNumber(),"The listed event conform to the expected event number");
        assertEquals(4, result.get(1).getEventNumber(),"The listed event conform to the expected event number");
        logout();

        //Cancel some events and search again
        cancelEvent1();

        // government active events
        // event 1 should now be missing from the list
        loginGovernment();
        result = listEvents(false, true);
        assertEquals(2, result.size(),"Number of active only events listed conform to the expected value " +
                "for government representative logged in");
        assertTrue(result.stream().noneMatch(
                (Event e) -> e.getEventNumber() == 1),"Event that are invalid for listing are not listed");
        logout();

        // provider events active only
        // provider 1 has only 1 active event left
        loginProvider1();
        result = listEvents(true, true);
        assertEquals(1, result.size(),"Number of user and active events listed conform " +
                "to the expected value for entertainment provider logged in");
        logout();

        // consumer events active only
        // only one active event fits Kanta's preferences
        loginKanta();
        result = listEvents(true, true);
        assertEquals(1, result.size(),"Number of user and active events listed conform " +
                "to the expected value for consumer logged in");
    }

    @Test
    void searchEventsOnGivenDateSuccessTest() {
        //get all events one day from now
        //all but event 3 should be returned
        List<Event> result = listEventsOnGivenDate(false, false,
                LocalDateTime.now().plusDays(1));
        assertEquals(3, result.size(),"Number of events listed on given day conform to the " +
                "expected value when no user logged in");
        assertTrue(result.stream().noneMatch(
                (Event e) -> e.getEventNumber() == 3),"Event that are invalid for listing are not listed");

        //get all events two days from now that match Kanta's preferences
        // should only return event 4
        loginKanta();
        result = listEventsOnGivenDate(true, false,
                LocalDateTime.now().plusDays(2));
        assertEquals(1, result.size(),"Number of user events listed on given day conform to the " +
                "expected value when consumer logged in");
        logout();

        // cancel event 1 and find provider 1's events 1 day from now
        // should only return event 2
        cancelEvent1();
        loginProvider1();
        result = listEventsOnGivenDate(true, true,
                LocalDateTime.now().plusDays(1));
        assertEquals(1, result.size(),"Number of user and active events listed on given date conform " +
                "to the expected value for entertainment provider logged in after previous event cancellation");
        assertEquals(2, result.get(0).getEventNumber(),"The listed event conform to the expected event number");
    }

    @Test
    void searchEventsOnGivenDateInvalidSearchDateTest() {
        //null date
        loginKanta();
        List<Event> result = listEventsOnGivenDate(false, false,
                null);
        assertNull(result,"Null listEventsOnGivenDate result for null searchDateTime input");

        //date in the past
        result = listEventsOnGivenDate(false, false,
                LocalDateTime.now().minusDays(1));
        assertNull(result,"Null listEventsOnGivenDate result for inputted date in the past");
    }
}
