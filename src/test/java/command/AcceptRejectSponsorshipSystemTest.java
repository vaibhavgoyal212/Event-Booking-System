package command;

import controller.Controller;
import logging.LogStatus;
import logging.Logger;
import model.EventType;
import model.SponsorshipRequest;
import model.SponsorshipStatus;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class AcceptRejectSponsorshipSystemTest {
    static Controller controller = new Controller();

    private static void loginProvider1() {
        controller.runCommand(new LoginCommand("anonymous@gmail.com", "anonymous"));
    }

    private static void loginProvider2() {
        controller.runCommand(new LoginCommand("e", "f"));
    }

    private static void registerProviderWithTwoEventsRequestingSponsorship() {
        controller.runCommand(new RegisterEntertainmentProviderCommand(
                "Olympics Committee",
                "Mt. Everest",
                "noreply@gmail.com",
                "Secret Identity",
                "anonymous@gmail.com",
                "anonymous",
                new ArrayList<String>(List.of("Unknown Actor", "Spy")),
                new ArrayList<String>(List.of("unknown@gmail.com", "spy@gmail.com"))
        ));

        CreateTicketedEventCommand eventCmd1 = new CreateTicketedEventCommand(
                "London Summer Olympics",
                EventType.Sports,
                123456,
                20,
                true
        );
        controller.runCommand(eventCmd1);
        long e1 = eventCmd1.getResult();
        controller.runCommand(new AddEventPerformanceCommand(
                e1,
                "ee",
                LocalDateTime.now().plusWeeks(1),
                LocalDateTime.now().plusWeeks(1).plusHours(2),
                Collections.emptyList(),
                false,
                true,
                false,
                50,
                50
        ));

        CreateTicketedEventCommand eventCmd2 = new CreateTicketedEventCommand(
                "Winter Olympics",
                EventType.Sports,
                40000,
                400,
                true
        );
        controller.runCommand(eventCmd2);
        long e2 = eventCmd2.getResult();
        controller.runCommand(new AddEventPerformanceCommand(
                e2,
                "e",
                LocalDateTime.now().plusWeeks(1),
                LocalDateTime.now().plusWeeks(1).plusHours(2),
                Collections.emptyList(),
                false,
                true,
                false,
                50,
                50
        ));

        controller.runCommand(new LogoutCommand());
    }

    private static void registerProviderWithEventInThePast() {
        controller.runCommand(new RegisterEntertainmentProviderCommand(
                "a",
                "b",
                "c",
                "d",
                "e",
                "f",
                new ArrayList<>(),
                new ArrayList<>()
        ));

        CreateTicketedEventCommand eventCmd1 = new CreateTicketedEventCommand(
                "e1",
                EventType.Sports,
                123456,
                20,
                true
        );
        controller.runCommand(eventCmd1);
        long eventNumber1 = eventCmd1.getResult();

        controller.runCommand(new AddEventPerformanceCommand(
                eventNumber1,
                "eee",
                LocalDateTime.now(),
                LocalDateTime.now(),
                Collections.emptyList(),
                false,
                true,
                false,
                50,
                50
        ));

        CreateTicketedEventCommand eventCmd2 = new CreateTicketedEventCommand(
                "Winter Olympics2",
                EventType.Sports,
                40000,
                400,
                true
        );
        controller.runCommand(eventCmd2);
        long e2 = eventCmd2.getResult();
        controller.runCommand(new AddEventPerformanceCommand(
                e2,
                "e",
                LocalDateTime.now().plusWeeks(2),
                LocalDateTime.now().plusWeeks(2).plusHours(2),
                Collections.emptyList(),
                false,
                true,
                false,
                50,
                50
        ));

        controller.runCommand(new LogoutCommand());
    }

    private static void cancelEvent4() {
        logout();
        loginProvider2();
        controller.runCommand(new CancelEventCommand(4, "hahaha"));
        logout();
    }

    private static void loginGovernmentRep() {
        controller.runCommand(new LoginCommand("margaret.thatcher@gov.uk", "The Good times  "));
    }

    private static void logout() {
        controller.runCommand(new LogoutCommand());
    }

    @BeforeEach
    void printTestName(TestInfo testInfo) {
        System.out.println(testInfo.getDisplayName());
    }

    @BeforeEach
    void resetController() {
        controller = new Controller();
        registerProviderWithTwoEventsRequestingSponsorship();
        registerProviderWithEventInThePast();
    }

    @AfterEach
    void clearLogs() {
        Logger.getInstance().clearLog();
        System.out.println("---");
        logout();
    }

    private List<SponsorshipRequest> getAllRequests() {
        ListSponsorshipRequestsCommand cmd = new ListSponsorshipRequestsCommand(false);
        controller.runCommand(cmd);
        return cmd.getResult();
    }

    private List<SponsorshipRequest> getPendingRequests() {
        ListSponsorshipRequestsCommand cmd = new ListSponsorshipRequestsCommand(true);
        controller.runCommand(cmd);
        return cmd.getResult();
    }

    @Test
    void verifyInitialStateTest() {
        List<SponsorshipRequest> requests = getAllRequests();

        assertEquals(0, requests.size(),"No requests are listed for user not logged in");

        loginGovernmentRep();
        requests = getAllRequests();

        assertEquals(4, requests.size(),"Number of requests conform to the setup");

        requests = getPendingRequests();

        assertEquals(4, requests.size(),"Number of pending requests conform to the setup");
    }

    @Test
    void acceptValidEventLoggedOutTest() {
        RespondSponsorshipCommand cmd1 = new RespondSponsorshipCommand(1, 5);
        controller.runCommand(cmd1);
        assertFalse(cmd1.getResult(),"False respond sponsorship result for user not logged in");

        loginGovernmentRep();
        List<SponsorshipRequest> requests = getAllRequests();

        assertEquals(SponsorshipStatus.PENDING, requests.get(0).getStatus(),"SponsorshipStatus conform to the expected value " +
                "after invalid sponsorship respond");
        assertEquals(0, requests.get(0).getSponsoredPricePercent(),"SponsoredPricePercent conform to the expected value " +
                "after invalid sponsorship respond");
        assertNull(requests.get(0).getSponsorAccountEmail(),"Null for SponsorAccountEmail after invalid sponsorship responding");
        assertFalse(requests.get(0).getEvent().isSponsored(),"isSponsored attribute conform to the expected value " +
                "after invalid sponsorship respond");
        assertEquals(requests.get(0).getEvent().getOriginalTicketPrice(),
                requests.get(0).getEvent().getDiscountedTicketPrice(),"OriginalTicketPrice same as DiscountedTicketPrice " +
                        "after invalid sponsorship respond");
    }

    @Test
    void acceptValidEventInvalidUserTest() {
        loginProvider1();
        RespondSponsorshipCommand cmd1 = new RespondSponsorshipCommand(1, 5);
        controller.runCommand(cmd1);
        assertFalse(cmd1.getResult(),"False respond sponsorship result for entertainment provider login");

        logout();

        loginGovernmentRep();
        List<SponsorshipRequest> requests = getAllRequests();

        assertEquals(SponsorshipStatus.PENDING, requests.get(0).getStatus(),"SponsorshipStatus conform to the expected value " +
                "after invalid sponsorship respond");
        assertEquals(0, requests.get(0).getSponsoredPricePercent(),"SponsoredPricePercent conform to the expected value " +
                "after invalid sponsorship respond");
        assertNull(requests.get(0).getSponsorAccountEmail(),"Null for SponsorAccountEmail after invalid sponsorship responding");
        assertFalse(requests.get(0).getEvent().isSponsored(),"isSponsored attribute conform to the expected value " +
                "after invalid sponsorship respond");
        assertEquals(requests.get(0).getEvent().getOriginalTicketPrice(),
                requests.get(0).getEvent().getDiscountedTicketPrice(),"OriginalTicketPrice same as DiscountedTicketPrice " +
                        "after invalid sponsorship respond");
    }

    @Test
    void acceptValidEventLoggedInTest() {
        loginGovernmentRep();
        List<SponsorshipRequest> requests = getAllRequests();

        // Accept
        RespondSponsorshipCommand cmd5 = new RespondSponsorshipCommand(1, 50);
        controller.runCommand(cmd5);

        // Verify state system is correct
        assertTrue(cmd5.getResult(),"True respond sponsorship result for valid Sponsorship respond");
        assertEquals(SponsorshipStatus.ACCEPTED, requests.get(0).getStatus(),"SponsorshipStatus conform to the expected value " +
                "after valid sponsorship respond");
        assertEquals(50, requests.get(0).getSponsoredPricePercent(),"SponsoredPricePercent conform to the expected value " +
                "after valid sponsorship respond");
        assertEquals("paymentEmail@help.com", requests.get(0).getSponsorAccountEmail(),"SponsorAccountEmail conform to the expected value " +
                "after valid sponsorship respond");
        assertTrue(requests.get(0).getEvent().isSponsored(),"isSponsored attribute conform to the expected value " +
                "after valid sponsorship respond");
        assertEquals(10, requests.get(0).getEvent().getDiscountedTicketPrice(),"Event's DiscountedTicketPrice conform to" +
                "the expected value after valid sponsorship respond");
        assertEquals(3, getPendingRequests().size(),"The number of pending requests conform to the expected value " +
                "after valid sponsorship respond");
        assertEquals(4, getAllRequests().size(),"The number of total requests conform to the expected value " +
                "after valid sponsorship respond");
    }

    @Test
    void rejectValidEventLoggedInTest() {
        loginGovernmentRep();
        List<SponsorshipRequest> requests = getAllRequests();

        // Accept
        RespondSponsorshipCommand cmd5 = new RespondSponsorshipCommand(1, 0);
        controller.runCommand(cmd5);

        // Verify state system is correct
        assertTrue(cmd5.getResult(),"True respond sponsorship result for valid Sponsorship respond");
        assertEquals(SponsorshipStatus.REJECTED, requests.get(0).getStatus(),"SponsorshipStatus conform to the expected value " +
                "after valid sponsorship respond");
        assertEquals(0, requests.get(0).getSponsoredPricePercent(),"SponsoredPricePercent conform to the expected value " +
                "after valid sponsorship respond");
        assertNull(requests.get(0).getSponsorAccountEmail(),"SponsorAccountEmail conform to the expected value " +
                "after valid sponsorship respond");
        assertFalse(requests.get(0).getEvent().isSponsored(),"isSponsored attribute conform to the expected value " +
                "after valid sponsorship respond");
        assertEquals(requests.get(0).getEvent().getOriginalTicketPrice(),
                     requests.get(0).getEvent().getDiscountedTicketPrice(),"OriginalTicketPrice same as DiscountedTicketPrice " +
                        "after rejecting sponsorship request");
        assertEquals(3, getPendingRequests().size(),"The number of pending requests conform to the expected value " +
                "after valid sponsorship respond");
        assertEquals(4, getAllRequests().size(),"The number of total requests conform to the expected value " +
                "after valid sponsorship respond");
    }

    @Test
    void respondForbiddenEventTest() {
        loginGovernmentRep();
        List<SponsorshipRequest> requests = getAllRequests();

        RespondSponsorshipCommand cmd5 = new RespondSponsorshipCommand(1, 50);
        controller.runCommand(cmd5);

        RespondSponsorshipCommand cmd4 = new RespondSponsorshipCommand(2, 0);
        controller.runCommand(cmd4);

        // Reject already accepted request
        RespondSponsorshipCommand cmd6 = new RespondSponsorshipCommand(1, 0);
        controller.runCommand(cmd6);
        assertFalse(cmd6.getResult(),"False respond sponsorship result for rejecting already accepted sponsorship request");
        assertEquals(SponsorshipStatus.ACCEPTED, requests.get(0).getStatus(),"SponsorshipStatus conform to the expected value " +
                "after invalid sponsorship respond");

        // Accept already rejected request
        RespondSponsorshipCommand cmd7 = new RespondSponsorshipCommand(2, 50);
        controller.runCommand(cmd7);
        assertFalse(cmd7.getResult(),"False respond sponsorship result for accepting already rejected sponsorship request");
        assertEquals(SponsorshipStatus.REJECTED, requests.get(1).getStatus(),"SponsorshipStatus conform to the expected value " +
                "after invalid sponsorship respond");

        //Accept sponsorship for event where all performances already happened
        RespondSponsorshipCommand cmd8 = new RespondSponsorshipCommand(3, 10);
        controller.runCommand(cmd8);
        assertFalse(cmd8.getResult(),"False respond sponsorship result for accepting sponsorship for event " +
                "where all performances already happened");

        //Accept sponsorship for an event that has been cancelled
        cancelEvent4();
        RespondSponsorshipCommand cmd9 = new RespondSponsorshipCommand(4, 10);
        controller.runCommand(cmd9);
        assertFalse(cmd9.getResult(),"False respond sponsorship result for accepting sponsorship for " +
                "an event that has been cancelled");

        controller.runCommand(new LogoutCommand());
    }

    @Test
    void respondInvalidParameters() {
        //Respond invalid request
        RespondSponsorshipCommand cmd2 = new RespondSponsorshipCommand(20, 10);
        controller.runCommand(cmd2);
        assertFalse(cmd2.getResult(),"False respond sponsorship result for a non-existing requestNumber");

        RespondSponsorshipCommand cmd5 = new RespondSponsorshipCommand(0, 10);
        controller.runCommand(cmd5);
        assertFalse(cmd5.getResult(),"False respond sponsorship result for requestNumber equals 0");

        RespondSponsorshipCommand cmd6 = new RespondSponsorshipCommand(-1, 10);
        controller.runCommand(cmd6);
        assertFalse(cmd6.getResult(),"False respond sponsorship result for negative requestNumber");

        //Respond invalid percentages
        RespondSponsorshipCommand cmd3 = new RespondSponsorshipCommand(1, -1);
        controller.runCommand(cmd3);
        assertFalse(cmd3.getResult(),"False respond sponsorship result for negative percentToSponsor");

        RespondSponsorshipCommand cmd4 = new RespondSponsorshipCommand(1, 101);
        controller.runCommand(cmd4);
        assertFalse(cmd4.getResult(),"False respond sponsorship result for percentToSponsor larger than 100");
    }
}
