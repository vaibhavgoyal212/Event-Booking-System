package state;

import model.EntertainmentProvider;
import model.EventType;
import model.SponsorshipRequest;
import model.TicketedEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class SponsorshipStateTest {
    SponsorshipState state = new SponsorshipState();

    EntertainmentProvider organiser = new EntertainmentProvider("Org 1", "Some Place",
            "provider1@payment.com", "Main Rep",
            "mainrep1@gmail.com", "password", new ArrayList<>(), new ArrayList<>()
    );

    TicketedEvent event1 = new TicketedEvent(1,
            new EntertainmentProvider("a", "a", "a", "a",
                    "a", "a", new ArrayList<>(), new ArrayList<>()),
            "t", EventType.Movie, 5, 5);

    TicketedEvent event2 = new TicketedEvent(2,
            new EntertainmentProvider("b", "b", "b", "b",
                    "b", "b", new ArrayList<>(),
                    new ArrayList<>()),
            "t2", EventType.Movie, 5, 5);

    TicketedEvent event3 = new TicketedEvent(3,
            new EntertainmentProvider("c", "c", "c", "c",
                    "c", "c", new ArrayList<>(),
                    new ArrayList<>()),
            "t3", EventType.Movie, 5, 5);

    @BeforeEach
    void resetState() {
        state = new SponsorshipState();
    }

    @Test
    void createBlankSponsorshipStateTest() {
        assertEquals(0, state.getAllSponsorshipRequests().size());
    }

    @Test
    void createSponsorshipStateFromOtherTest() {
        SponsorshipState oldState = new SponsorshipState();
        oldState.addSponsorshipRequest(event1);

        SponsorshipState newState = new SponsorshipState(oldState);
        newState.addSponsorshipRequest(event2);

        assertEquals(2, newState.getAllSponsorshipRequests().size(),"Number of SponsorshipRequests in new state conform to the expected value");
        assertNotNull(newState.findRequestByNumber(1),"SponsorshipRequest in new state was found by valid requestNumber");
        assertNotNull(newState.findRequestByNumber(2),"SponsorshipRequest in new state was found by valid requestNumber");
    }

    @Test
    void addSponsorshipRequestValidEventTest() {
        SponsorshipRequest newRequest = state.addSponsorshipRequest(event1);

        assertEquals(1, state.getAllSponsorshipRequests().size(),"Number of SponsorshipRequests in sponsorship state conform to the " +
                "expected value after adding valid sponsorshipRequest");
        assertNotNull(state.findRequestByNumber(1),"SponsorshipRequest in sponsorship state was found by valid requestNumber");
        assertNotNull(newRequest,"Not null for addSponsorshipRequest result after adding valid sponsorshipRequest");
    }

    @Test
    void addSponsorshipRequestNullEventTest() {
        SponsorshipRequest newRequest = state.addSponsorshipRequest(null);

        assertNull(newRequest,"Null for addSponsorshipRequest result after passing null event as input");
        assertEquals(0, state.getAllSponsorshipRequests().size(),"Number of SponsorshipRequests in sponsorship state conform to the " +
                "expected value after invalid addSponsorshipRequest");
    }

    @Test
    void addSponsorshipRequestNullEventAndValidEventTest() {
        state.addSponsorshipRequest(null);

        SponsorshipRequest newRequest = state.addSponsorshipRequest(event1);

        assertNotNull(newRequest,"Not null for addSponsorshipRequest result after invalid followed by valid addSponsorshipRequest");
        assertEquals(1, newRequest.getRequestNumber(),"Number of SponsorshipRequests in sponsorship state conform to the " +
                "expected value after invalid followed by valid addSponsorshipRequest");
    }

    @Test
    void addSponsorshipRequestValidEventAndNullEventTest() {
        state.addSponsorshipRequest(event1);

        SponsorshipRequest newRequest = state.addSponsorshipRequest(null);

        assertEquals(1, state.getAllSponsorshipRequests().size(),"Number of SponsorshipRequests in sponsorship state conform to the " +
                "expected value after valid followed by invalid addSponsorshipRequest");
    }

    @Test
    void findRequestByNumberValidNumberTest() {
        state.addSponsorshipRequest(event1);

        assertNotNull(state.findRequestByNumber(1),"Not null for findRequestByNumber result by passing valid requestNumber");
    }

    @Test
    void findRequestByNumber0Test() {
        state.addSponsorshipRequest(event1);

        assertNull(state.findRequestByNumber(0),"Null for findRequestByNumber result by passing requestNumber equals 0");
    }

    @Test
    void findRequestByNumberPlus1Test() {
        state.addSponsorshipRequest(event1);

        assertNull(state.findRequestByNumber(2),"Null for findRequestByNumber result by passing non-existing requestNumber");
    }

    @Test
    void findRequestByNegativeNumberTest() {
        state.addSponsorshipRequest(event1);

        assertNull(state.findRequestByNumber(-1),"Null for findRequestByNumber result by passing negative requestNumber");
    }

    @Test
    void getAllSponsorshipRequests() {
        state.addSponsorshipRequest(event1);
        state.addSponsorshipRequest(event2);

        assertEquals(2, state.getAllSponsorshipRequests().size(),"Number of SponsorshipRequests in getAllSponsorshipRequests result " +
                "conform to the expected value after valid addSponsorshipRequest");
        assertEquals(event1, state.getAllSponsorshipRequests().get(0).getEvent(),"SponsorshipRequest in getAllSponsorshipRequests result " +
                "conform to the expected SponsorshipRequest");
        assertEquals(event2, state.getAllSponsorshipRequests().get(1).getEvent(),"SponsorshipRequest in getAllSponsorshipRequests result " +
                "conform to the expected SponsorshipRequest");
    }

    @Test
    void getPendingSponsorshipRequests() {
        SponsorshipRequest r1 = state.addSponsorshipRequest(event1);
        SponsorshipRequest r2 = state.addSponsorshipRequest(event2);
        SponsorshipRequest r3 = state.addSponsorshipRequest(event3);

        event1.setSponsorshipRequest(r1);
        event2.setSponsorshipRequest(r2);
        event2.setSponsorshipRequest(r3);

        r1.reject();
        r2.accept(5, "whatever");

        assertEquals(1, state.getPendingSponsorshipRequests().size(),"Number of pending SponsorshipRequests in getPendingSponsorshipRequests result" +
                " conform to the expected value after rejecting and accepting sponsorship");
        assertEquals(3, state.getPendingSponsorshipRequests().get(0).getRequestNumber(),"Pending sponsorshipRequest in getPendingSponsorshipRequests result " +
                "conform to the expected pending sponsorshipRequest");
    }
}