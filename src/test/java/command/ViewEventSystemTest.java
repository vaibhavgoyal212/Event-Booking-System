package command;

import controller.Controller;
import logging.Logger;
import model.Event;
import model.EventType;
import org.junit.jupiter.api.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;


public class ViewEventSystemTest {
    private static void createUsers(Controller controller) {
        controller.runCommand(new RegisterConsumerCommand("gajodhar", "gajoocool@gmail.com",
                "+447345678123", "gajosinghcool", "gajjopays@pay.com"));
        controller.runCommand(new LogoutCommand());

        controller.runCommand(new RegisterConsumerCommand("jacque", "jackpigeon@gmail.com",
                "+447345643521", "jacquewrites", "jacquemoolah@pay.com"));
        controller.runCommand(new LogoutCommand());

        controller.runCommand(new RegisterConsumerCommand("kanta", "kantabai@gmail.com",
                "+447334679078", "kantakichaat", "kantakikharchi@pay.com"));
        controller.runCommand(new LogoutCommand());
    }

    private static void loginUser(String email, String password, Controller controller) {
        LoginCommand cmd = new LoginCommand(email, password);
        controller.runCommand(cmd);
    }

    private static void createDanceFestWith2Events(Controller controller) {
        controller.runCommand(new RegisterEntertainmentProviderCommand("DanceMania", "St James Quarters, Edinburgh",
                "weneedmoney@gmail.com", "Mark Stonie",
                "MarkStonieEats@gmail.com", "SirMarksEatALot", new ArrayList<>(List.of("Jack Pog")), new ArrayList<>(List.of("jackpoggers@gmail.com"))));


        CreateTicketedEventCommand eventCmd1 = new CreateTicketedEventCommand("HipHop Hosedown", EventType.Dance, 1000, 15, false);

        controller.runCommand(eventCmd1);
        long event1number = eventCmd1.getResult();


        controller.runCommand(new AddEventPerformanceCommand(event1number, "Swan Lake Holyrood", LocalDateTime.now().plusMonths(3),
                LocalDateTime.now().plusMonths(3).plusHours(5), Collections.emptyList(),
                true, true, true, 600, 800));


        controller.runCommand(new AddEventPerformanceCommand(event1number, "Odean theatre", LocalDateTime.now().plusMonths(3),
                LocalDateTime.now().plusMonths(3).plusHours(5), Collections.emptyList(),
                true, true, false, 400, 1000));

        CreateTicketedEventCommand eventCmd2 = new CreateTicketedEventCommand("Ballet Bounce", EventType.Dance, 500, 30, false);

        controller.runCommand(eventCmd2);
        long event2number = eventCmd2.getResult();

        //performance for event2
        controller.runCommand(new AddEventPerformanceCommand(event2number, "The balmoral ballroom", LocalDateTime.now().plusMonths(4),
                LocalDateTime.now().plusMonths(4).plusHours(8), Collections.emptyList(),
                true, true, false, 500, 900));

        controller.runCommand(new LogoutCommand());

    }

    private static void createBuskingProviderWith1Event(Controller controller) {
        controller.runCommand(new RegisterEntertainmentProviderCommand(
                "BuskersOrg",
                "Leith Walk",
                "buskergetsmoney@gmail.com",
                "the best musicican ever",
                "busk@every.day",
                "When they say 'you can't do this': Ding Dong! You are wrong!",
                new ArrayList<>(),
                new ArrayList<>()
        ));

        CreateTicketedEventCommand eventCmd = new CreateTicketedEventCommand(
                "Music for everyone!",
                EventType.Music, 200, 30, false);
        controller.runCommand(eventCmd);
        long eventNumber = eventCmd.getResult();

        controller.runCommand(new AddEventPerformanceCommand(
                eventNumber,
                "Leith as usual",
                LocalDateTime.of(2030, 3, 20, 4, 20),
                LocalDateTime.of(2030, 3, 20, 6, 45),
                List.of("The same musician"),
                true,
                true,
                true,
                Integer.MAX_VALUE,
                Integer.MAX_VALUE
        ));
        controller.runCommand(new AddEventPerformanceCommand(
                eventNumber,
                "You know it",
                LocalDateTime.of(2030, 3, 21, 4, 20),
                LocalDateTime.of(2030, 3, 21, 7, 0),
                List.of("The usual"),
                true,
                true,
                true,
                Integer.MAX_VALUE,
                Integer.MAX_VALUE
        ));

        controller.runCommand(new LogoutCommand());
    }

    private static Integer getTicketsforEventPerformance(Integer eventNum, Integer perfNum, Controller controller) {
        GetAvailablePerformanceTicketsCommand cmd = new GetAvailablePerformanceTicketsCommand(eventNum, perfNum);
        controller.runCommand(cmd);
        return cmd.getResult();
    }

    private static Event getEventFromNumber(long eventNum, Controller controller) {
        Event result = null;
        ListEventsCommand eventlist = new ListEventsCommand(false, false);
        controller.runCommand(eventlist);
        List<Event> allEvents = eventlist.getResult();
        for (Event e : allEvents) {
            Long eNum = e.getEventNumber();
            if (eNum.equals(eventNum)) {
                result = e;
                break;
            }

        }
        return result;
    }

    private static Controller controller = new Controller();

    @BeforeEach
    void printTestName(TestInfo testInfo) {
        System.out.println(testInfo.getDisplayName());
    }

    @BeforeEach
    void setupScenario() {
        controller = new Controller();
        createUsers(controller);
        createDanceFestWith2Events(controller);
        createBuskingProviderWith1Event(controller);

        loginUser("gajoocool@gmail.com", "gajosinghcool", controller);
        BookEventCommand Cmd1 = new BookEventCommand(1, 1, 10);
        BookEventCommand Cmd2 = new BookEventCommand(1, 2, 40);
        controller.runCommand(Cmd1);
        controller.runCommand(Cmd2);
        Long booking1 = Cmd1.getResult();
        Long booking2 = Cmd2.getResult();

        loginUser("kantabai@gmail.com", "kantakichaat", controller);
        BookEventCommand bookCmd1 = new BookEventCommand(1, 1, 90);
        BookEventCommand bookCmd4 = new BookEventCommand(2, 3, 32);
        controller.runCommand(bookCmd1);
        controller.runCommand(bookCmd4);
        Long booking3 = bookCmd1.getResult();
        Long booking4 = bookCmd4.getResult();

        loginUser("jackpigeon@gmail.com", "jacquewrites", controller);
        BookEventCommand bookCmd3 = new BookEventCommand(2, 3, 20);
        controller.runCommand(bookCmd3);
        Long booking5 = bookCmd3.getResult();
    }

    @AfterEach
    void clearLogs() {
        Logger.getInstance().clearLog();
        System.out.println("---");
    }

    @Test
    void viewEventInvalidInputTests() {
        Integer nullPerf = getTicketsforEventPerformance(3, 20, controller);
        Integer nullEvent = getTicketsforEventPerformance(4, 2, controller);

        assertNull(nullEvent, "no tickets found for invalid eventNumber");
        assertNull(nullPerf, "no tickets found for invalid performance Number");
    }

    @Test
    @DisplayName("view Event test")
    void viewEventSuccess() {
        Integer ticksEvent1Perf1 = getTicketsforEventPerformance(1, 1, controller);
        Integer ticksEvent2Perf3 = getTicketsforEventPerformance(2, 3, controller);
        Integer ticksEvent3Perf4 = getTicketsforEventPerformance(3, 4, controller);

        String output1 = getEventFromNumber(1, controller).toString() + "availableTickets=" + ticksEvent1Perf1;
        String output2 = getEventFromNumber(2, controller).toString() + "availableTickets=" + ticksEvent2Perf3;
        String output3 = getEventFromNumber(3, controller).toString() + "availableTickets=" + ticksEvent3Perf4;

        assertEquals("TicketedEvent{eventNumber=1, Organiser=DanceMania, title=HipHop " +
                "Hosedown, ticket price=15.0} availableTickets=860", output1,"The view event output conform to the expected value");
        assertEquals("TicketedEvent{eventNumber=2, Organiser=DanceMania, title=Ballet Bounce, " +
                "ticket price=30.0} availableTickets=448", output2,"The view event output conform to the expected value");
        assertEquals("TicketedEvent{eventNumber=3, Organiser=BuskersOrg, title=Music for " +
                "everyone!, ticket price=30.0} availableTickets=200", output3,"The view event output conform to the expected value");
    }
}
