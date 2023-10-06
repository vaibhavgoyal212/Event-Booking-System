package command;

import controller.Controller;
import logging.Logger;
import model.EventType;
import model.SponsorshipRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CancelEventSystemTest {
    /*
    * Setup state:
    * Register Provider 1 and Provider 1 Create one ticketed Event(1), not requesting for sponsorship, one future performance;
    * Register Provider 2 and Provider 2 Create two ticketed sponsored Events(2, 3), both requesting for sponsorship,
    * event 3 has one future performance while event 3 has one already started and one in the future.
    *
    *
    * Government Login - Accept All Sponsorships - Logout;
    *
    * Register Consumer 1 - Login - Book event 1- Logout;
    * Register Consumer 2 - Login - Book event 2 3 - Logout;
    *
    *
    * TO TEST:
    * No Blank organiser message.
    * Only Entertainment provider can cancel event, Not Consumer / Gov rep.
      AND Logged-in user is the organiser of the event.
    * The event should be active.
    * The event has no performances that have already started or ended.
    * If ticketed, sponsorship amount is successfully refunded to the government.
    * */

    private Controller controller = null;
    private Long event1Number = (long) -1;
    private Long event2Number = (long) -1;
    private Long event3Number = (long) -1;
    private Long bookingNum1 = (long) -1;
    private Long bookingNum2 = (long) -1;
    private Long bookingNum3 = (long) -1;

    @BeforeEach
    void printTestNameAndSetUp(TestInfo testInfo) {
        System.out.println(testInfo.getDisplayName());
        setUp();

    }

    // Set up some event / booking to cancel
    private void setUp() {
        controller = new Controller();
        createProvider1With1NonSponsoredEvent(controller);
        createProvider2With2SponsoredEvent(controller);

        loginGovernmentRepresentative(controller);
        governmentAcceptAllSponsorships(controller);
        controller.runCommand(new LogoutCommand());

        registerConsumer1AndBookNonSponsoredEvent(controller);
        registerConsumer2AndBookSponsoredEvents(controller);
    }

    @AfterEach
    void clearLogs() {
        Logger.getInstance().clearLog();
        System.out.println("---");
    }

    private void createProvider1With1NonSponsoredEvent(Controller controller) {
        // Register Provider1
        controller.runCommand(new RegisterEntertainmentProviderCommand(
                "Org 1",
                "Some Place",
                "provider1@payment.com",
                "Main Rep One",
                "mainrep1@gmail.com",
                "password",
                new ArrayList<>(),
                new ArrayList<>()
        ));

        // Create a non-sponsored event
        CreateTicketedEventCommand eventCmd = new CreateTicketedEventCommand(
                "Need ticket!",
                EventType.Music,
                32767,
                100,
                false
        );
        controller.runCommand(eventCmd);
        event1Number = eventCmd.getResult();

        // Add performance
        controller.runCommand(new AddEventPerformanceCommand(
                event1Number,
                "Same as usual",
                LocalDateTime.of(2552, 4, 18, 11, 59),
                LocalDateTime.of(2552, 4, 18, 12, 1),
                List.of("PerfName1", "PerfName2"),
                true,
                true,
                true,
                Integer.MAX_VALUE,
                Integer.MAX_VALUE
        ));

        controller.runCommand(new LogoutCommand());
    }

    private void createProvider2With2SponsoredEvent(Controller controller) {
        // Register Provider2
        controller.runCommand(new RegisterEntertainmentProviderCommand(
                "Org 2",
                "Some Place2",
                "provider2@payment.com",
                "Main Rep Two",
                "mainrep2@gmail.com",
                "password",
                new ArrayList<>(),
                new ArrayList<>()
        ));

        // Create ticked event 2
        CreateTicketedEventCommand event2Cmd = new CreateTicketedEventCommand(
                "Ticketed!",
                EventType.Sports,
                1234,
                100,
                true
        );
        controller.runCommand(event2Cmd);
        event2Number = event2Cmd.getResult();

        // Add performance for event 2
        controller.runCommand(new AddEventPerformanceCommand(
                event2Number,
                "Same as usual",
                LocalDateTime.of(2552, 4, 12, 11, 59),
                LocalDateTime.of(2552, 4, 12, 12, 1),
                List.of("PerfName"),
                true,
                true,
                true,
                Integer.MAX_VALUE,
                Integer.MAX_VALUE
        ));


        // Create ticked event 3
        CreateTicketedEventCommand event3Cmd = new CreateTicketedEventCommand(
                "Still Ticketed haha",
                EventType.Movie,
                343,
                150,
                true
        );
        controller.runCommand(event3Cmd);
        event3Number = event3Cmd.getResult();

        // Add performance for event 3
        controller.runCommand(new AddEventPerformanceCommand(
                event3Number,
                "Same as usual2",
                LocalDateTime.of(2552, 4, 12, 10, 59),
                LocalDateTime.of(2552, 4, 12, 11, 1),
                List.of("IHaveNoName"),
                true,
                true,
                false,
                750,
                3090
        ));

        // Add an already started performance
        controller.runCommand(new AddEventPerformanceCommand(
                event3Number,
                "Same as usual3",
                LocalDateTime.of(2021, 4, 12, 11, 59),
                LocalDateTime.of(2552, 4, 12, 12, 1),
                List.of("PerfName"),
                true,
                true,
                true,
                Integer.MAX_VALUE,
                Integer.MAX_VALUE
        ));

        controller.runCommand(new LogoutCommand());
    }

    private void loginGovernmentRepresentative(Controller controller) {
        controller.runCommand(new LoginCommand("gov.rep@gov.uk", "shortPassword"));
    }

    private void governmentAcceptAllSponsorships(Controller controller) {
        ListSponsorshipRequestsCommand cmd = new ListSponsorshipRequestsCommand(true);
        controller.runCommand(cmd);
        List<SponsorshipRequest> requests = cmd.getResult();
        for (SponsorshipRequest request : requests) {
            controller.runCommand(new RespondSponsorshipCommand(request.getRequestNumber(), 50));
        }
    }

    private void registerConsumer1AndBookNonSponsoredEvent(Controller controller) {
        // Register Consumer 1
        controller.runCommand(new RegisterConsumerCommand(
                "Consumer1",
                "consumer1@personal.com",
                "4407123456789",
                "password1",
                "consumer1@payment.com"));
        controller.runCommand(new LogoutCommand());

        // Login Consumer 1
        controller.runCommand(new LoginCommand("consumer1@personal.com", "password1"));

        // Book Non-sponsored event 1
        BookEventCommand cmd1 = new BookEventCommand(event1Number, event1Number, 10);
        controller.runCommand(cmd1);
        bookingNum1 = cmd1.getResult();

        // Logout
        controller.runCommand(new LogoutCommand());
    }

    private void registerConsumer2AndBookSponsoredEvents(Controller controller) {
        // Register Consumer 2
        controller.runCommand(new RegisterConsumerCommand(
                "Consumer2",
                "consumer2@personal.com",
                "4407123456790",
                "password2",
                "consumer2@payment.com"));
        controller.runCommand(new LogoutCommand());

        // Login Consumer 2
        controller.runCommand(new LoginCommand("consumer2@personal.com", "password2"));

        // Book sponsored event 2
        BookEventCommand cmd2 = new BookEventCommand(event2Number, event2Number, 10);
        controller.runCommand(cmd2);
        bookingNum2 = cmd2.getResult();

        // Book sponsored event 3
        BookEventCommand cmd3 = new BookEventCommand(event2Number, event2Number, 10);
        controller.runCommand(cmd3);
        bookingNum3 = cmd3.getResult();
        // Logout
        controller.runCommand(new LogoutCommand());
    }

    private void loginProvider1(Controller controller) {
        controller.runCommand(new LoginCommand("mainrep1@gmail.com", "password"));
    }

    private void loginProvider2(Controller controller) {
        controller.runCommand(new LoginCommand("mainrep2@gmail.com", "password"));
    }

    private void loginConsumer1(Controller controller) {
        controller.runCommand(new LoginCommand("consumer1@personal.com", "password1"));
    }

    private void loginConsumer2(Controller controller) {
        controller.runCommand(new LoginCommand("consumer2@personal.com", "password2"));
    }

    @Test
    void organiserMessageTest() {
        loginProvider1(controller);

        // Try blank msg
        CancelEventCommand cmd1 = new CancelEventCommand(bookingNum1, "  ");
        controller.runCommand(cmd1);
        assertFalse(cmd1.getResult(),"False CancelEvent result for empty organiserMessage");

        // Try null
        CancelEventCommand cmd2 = new CancelEventCommand(bookingNum1, null);
        controller.runCommand(cmd2);
        assertFalse(cmd2.getResult(),"False CancelEvent result for null organiserMessage");

        // Try Normal string
        CancelEventCommand cmd3 = new CancelEventCommand(bookingNum1, "I don't like this event.");
        controller.runCommand(cmd3);
        assertTrue(cmd3.getResult(),"False CancelEvent result for valid organiserMessage");
    }

    @Test
    void eventProviderCanCancelTest() {

        // Provider 2 try cancelling provider 1's event
        loginProvider2(controller);
        CancelEventCommand cmd1 = new CancelEventCommand(bookingNum1, "I don't like this event.");
        controller.runCommand(cmd1);
        assertFalse(cmd1.getResult(),"False CancelEvent result for provider cancelling other provider's event");
        controller.runCommand(new LogoutCommand());

        // Consumer try cancel
        loginConsumer1(controller);
        CancelEventCommand cmd2 = new CancelEventCommand(bookingNum1, "I don't like this event.");
        controller.runCommand(cmd2);
        assertFalse(cmd2.getResult(),"False CancelEvent result for consumer cancelling event");
        controller.runCommand(new LogoutCommand());

        // Gov try cancel
        loginGovernmentRepresentative(controller);
        CancelEventCommand cmd3 = new CancelEventCommand(bookingNum1, "I don't like this event.");
        controller.runCommand(cmd3);
        assertFalse(cmd3.getResult(),"False CancelEvent result for government cancelling event");
        controller.runCommand(new LogoutCommand());

        // Provider 1 try cancelling own event
        loginProvider1(controller);
        CancelEventCommand cmd4 = new CancelEventCommand(bookingNum1, "I don't like this event.");
        controller.runCommand(cmd4);
        assertTrue(cmd4.getResult(),"True CancelEvent result for Provider cancelling its own event");
        controller.runCommand(new LogoutCommand());
    }

    @Test
    void onlyActiveEventCanBeCancelledTest() {
        loginProvider2(controller);

        // Provider 2 cancelling active event 2
        CancelEventCommand cmd1 = new CancelEventCommand(bookingNum2, "I don't like this event.");
        controller.runCommand(cmd1);
        assertTrue(cmd1.getResult(),"True CancelEvent result for Provider cancelling active event"); // Should be able to cancel

        // Provider 2 try cancelling inactive event 2 again
        CancelEventCommand cmd2 = new CancelEventCommand(bookingNum2, "I don't like this event.");
        controller.runCommand(cmd2);
        assertFalse(cmd2.getResult(),"False CancelEvent result for Provider cancelling inactive event"); // Should NOT be able to cancel
    }

    @Test
    void noCancellingWhenAlreadyStartedOrEnded() {
        loginProvider2(controller); // Provider 2 created both event 2 and event 3

        // Event 3 (with bookingNum3) have a performance already started
        CancelEventCommand cmd1 = new CancelEventCommand(bookingNum3, "I don't like this event.");
        controller.runCommand(cmd1);
        assertFalse(cmd1.getResult(),"False CancelEvent result for one of performances in the event already started"); // Can't cancel

        // NOTE:
        // Here, no need to test if a performance has ended, because an ended performance must be started before the
        // current time. (verifies by AddEventPerformanceCommand: performance startDateTime is not after endDateTime)
    }
}