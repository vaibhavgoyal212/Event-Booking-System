package state;

import model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class EventStateTest {
    EventState state = new EventState();

    EntertainmentProvider provider1 = new EntertainmentProvider("DanceMania",
            "St James Quarters, Edinburgh", "weneedmoney@gmail.com",
            "Mark Stonie", "MarkStonieEats@gmail.com",
            "SirMarksEatALot", new ArrayList<>(), new ArrayList<>());

    EntertainmentProvider provider2 = new EntertainmentProvider("BuskersOrg",
            "Leith Walk", "buskergetsmoney@gmail.com",
            "the best musicican ever", "busk@every.day", "wrong!",
            new ArrayList<>(), new ArrayList<>());

    @BeforeEach
    void resetState() {
        state = new EventState();
    }

    @Test
    void createNewStateTest() {
        EventState eventState = new EventState();

        assertTrue(eventState.getAllEvents().isEmpty(), "event state initialised correctly");
    }

    @Test
    void createStateFromOtherTest() {
        EventState eventState1 = new EventState();

        eventState1.createTicketedEvent(provider1, "HipHopHoser", EventType.Dance, 15, 100);
        eventState1.createNonTicketedEvent(provider2, "buskIt", EventType.Music);

        EventState eventState2 = new EventState(eventState1);
        eventState2.createNonTicketedEvent(provider1, "wwww", EventType.Theatre);

        List<Event> events1 = eventState1.getAllEvents();
        List<Event> events2 = eventState2.getAllEvents();

        assertNotSame(events1, events2,"");
        assertEquals(3, events2.size(),"");
        assertEquals(3, events2.get(2).getEventNumber());
    }

    @Test
    void createValidNonTicketedEventTest() {
        NonTicketedEvent buskEv1 = state.createNonTicketedEvent(provider1, "buskIt", EventType.Music);

        assertTrue(state.getAllEvents().contains(buskEv1),"NonTicketedEvent added to event state after valid event creation");
        assertEquals(1, buskEv1.getEventNumber(),"Number of events in event state conform to the expected value after event creation");
    }

    @Test
    void createValidNonTicketedEventAndNullEventTest() {
        NonTicketedEvent danceEv1 = state.createNonTicketedEvent(provider1, "HipHopHoser",
                EventType.Dance);

        NonTicketedEvent danceEv2 = state.createNonTicketedEvent(null, "HipHopHoser",
                EventType.Dance);

        assertTrue(state.getAllEvents().contains(danceEv1),"NonTicketedEvent added to event state after valid followed by invalid event creation");
        assertEquals(1, danceEv1.getEventNumber(),"Event number conform to the expected value after valid followed by invalid event creation");
        assertNull(danceEv2,"Null createNonTicketedEvent result for null input");
        assertEquals(1, state.getAllEvents().size(),"Number of events in event state conform to the expected value " +
                "after valid followed by invalid event creation");
    }

    @Test
    void createValidNullEventAndNonTicketedEventTest() {
        NonTicketedEvent danceEv2 = state.createNonTicketedEvent(null, "HipHopHoser",
                EventType.Dance);

        NonTicketedEvent danceEv1 = state.createNonTicketedEvent(provider1, "HipHopHoser",
                EventType.Dance);

        assertTrue(state.getAllEvents().contains(danceEv1),"NonTicketedEvent added to event state after invalid followed by valid event creation");
        assertEquals(1, danceEv1.getEventNumber(),"Event number conform to the expected value after invalid followed by valid event creation");
        assertNull(danceEv2,"Null createNonTicketedEvent result for null input");
        assertEquals(1, state.getAllEvents().size(),"Number of events in event state conform to the expected value " +
                "after invalid followed by valid event creation");
    }

    @Test
    void createNonTicketedEventNullOrganiserTest() {
        NonTicketedEvent buskEv1 = state.createNonTicketedEvent(null, "buskIt", EventType.Music);

        assertNull(buskEv1,"Null createNonTicketedEvent result for null organiser input");
    }

    @Test
    void createNonTicketedEventNullTitleTest() {
        NonTicketedEvent buskEv2 = state.createNonTicketedEvent(provider1, null, EventType.Music);

        assertNull(buskEv2,"Null createNonTicketedEvent result for null title input");
    }

    @Test
    void createNonTicketedEventNullTypeTest() {
        NonTicketedEvent buskEv3 = state.createNonTicketedEvent(provider1, "buskIt", null);

        assertNull(buskEv3,"Null createNonTicketedEvent result for null type input");
    }

    @Test
    void createNonTicketedEventEmptyTitleTest() {
        NonTicketedEvent buskEv1 = state.createNonTicketedEvent(provider1, "  ",
                EventType.Music);

        assertNull(buskEv1,"Null createNonTicketedEvent result for empty title input");
    }

    @Test
    void createValidTicketedEventTest() {
        TicketedEvent danceEv1 = state.createTicketedEvent(provider1, "HipHopHoser",
                EventType.Dance, 15, 100);

        assertTrue(state.getAllEvents().contains(danceEv1),"TicketedEvent added to event state after valid event creation");
        assertEquals(1, danceEv1.getEventNumber(),"Number of events in event state conform to the expected " +
                "value after valid event creation");
    }

    @Test
    void createValidTicketedEventAndNullEventTest() {
        TicketedEvent danceEv1 = state.createTicketedEvent(provider1, "HipHopHoser",
                EventType.Dance, 15, 100);

        TicketedEvent danceEv2 = state.createTicketedEvent(null, "HipHopHoser",
                EventType.Dance, 15, 100);

        assertTrue(state.getAllEvents().contains(danceEv1),"TicketedEvent added to event state after valid followed by " +
                "invalid Ticketed event creation");
        assertEquals(1, danceEv1.getEventNumber(),"Event number conform to the expected value after valid followed by " +
                "invalid Ticketed event creation");
        assertNull(danceEv2,"Null createTicketedEvent result for null input");
        assertEquals(1, state.getAllEvents().size(),"Number of events in event state conform to the expected value " +
                "after valid followed by invalid Ticketed event creation");
    }

    @Test
    void createValidNullEventAndTicketedEventTest() {
        state.createTicketedEvent(null, "HipHopHoser",
                EventType.Dance, 15, 100);

        TicketedEvent danceEv1 = state.createTicketedEvent(provider1, "HipHopHoser",
                EventType.Dance, 15, 100);

        assertTrue(state.getAllEvents().contains(danceEv1),"TicketedEvent added to event state after invalid followed by " +
                "valid Ticketed event creation");
        assertEquals(1, danceEv1.getEventNumber(),"Event number conform to the expected value after invalid followed by " +
                "valid Ticketed event creation");
        assertEquals(1, state.getAllEvents().size(),"Number of events in event state conform to the expected value " +
                "after invalid followed by valid Ticketed event creation");
    }

    @Test
    void createTicketedEventNullOrganiserTest() {
        TicketedEvent danceEv1 = state.createTicketedEvent(null, "HipHopHoser",
                EventType.Dance, 15,
                100);

        assertNull(danceEv1,"Null createTicketedEvent result for null organiser input");
    }

    @Test
    void createTicketedEventNullTitleTest() {
        TicketedEvent danceEv2 = state.createTicketedEvent(provider2, null, EventType.Dance,
                15,
                100);

        assertNull(danceEv2,"Null createTicketedEvent result for null title input");
    }

    @Test
    void createTicketedEventNullTypeTest() {
        TicketedEvent danceEv3 = state.createTicketedEvent(provider2, "HipHopHoser", null,
                15, 100);

        assertNull(danceEv3,"Null createTicketedEvent result for null type input");
    }

    @Test
    void createTicketedEventEmptyTitleTest() {
        TicketedEvent danceEv1 = state.createTicketedEvent(provider1, "   ",
                EventType.Dance, 15, 100);

        assertNull(danceEv1,"Null createTicketedEvent result for empty type input");
    }

    @Test
    void createTicketedEvent0TicketPriceTest() {
        TicketedEvent danceEv2 = state.createTicketedEvent(provider1, "HipHopHoser",
                EventType.Dance, 0, 100);

        assertNull(danceEv2,"Null createTicketedEvent result for ticketPrice equals 0");
    }

    @Test
    void createTicketedEventNegativeTicketPriceTest() {
        TicketedEvent danceEv2 = state.createTicketedEvent(provider1, "HipHopHoser",
                EventType.Dance, -1, 100);

        assertNull(danceEv2,"Null createTicketedEvent result for negative ticketPrice input");
    }

    @Test
    void createTicketedEvent0TicketsTest() {
        TicketedEvent danceEv3 = state.createTicketedEvent(provider1, "HipHopHoser",
                EventType.Dance, 15, 0);

        assertNull(danceEv3,"Null createTicketedEvent result for numTickets equals 0");
    }

    @Test
    void createTicketedEventNegativeTicketsTest() {
        TicketedEvent danceEv3 = state.createTicketedEvent(provider1, "HipHopHoser",
                EventType.Dance, 15, -1);

        assertNull(danceEv3,"Null createTicketedEvent result for negative numTickets");
    }

    @Test
    void createValidPerformanceTest() {
        TicketedEvent danceEv1 = state.createTicketedEvent(provider1, "HipHopHoser",
                EventType.Dance, 15, 100);

        EventPerformance danceP1 = state.createEventPerformance(danceEv1, "testaddress",
                LocalDateTime.now().plusDays(15), LocalDateTime.now().plusDays(15).plusHours(4),
                List.of("michael jackson"), true, true, true,
                300, 400);

        assertEquals(1, danceP1.getPerformanceNumber(),"Performance number conform to the expected value after valid performance creation");
        assertEquals(1, danceEv1.getPerformances().size(),"Number of performance of the event conform to the expected value " +
                "after valid performance creation");
    }

    @Test
    void createValidPerformanceAndNullPerformanceTest() {
        TicketedEvent danceEv1 = state.createTicketedEvent(provider1, "HipHopHoser",
                EventType.Dance, 15, 100);

        EventPerformance danceP1 = state.createEventPerformance(danceEv1, "testaddress",
                LocalDateTime.now().plusDays(15), LocalDateTime.now().plusDays(15).plusHours(4),
                List.of("michael jackson"), true, true, true,
                300, 400);

        state.createEventPerformance(null, "testaddress",
                LocalDateTime.now().plusDays(15), LocalDateTime.now().plusDays(15).plusHours(4),
                List.of("michael jackson"), true, true, true,
                300, 400);

        assertEquals(1, danceP1.getPerformanceNumber(),"Performance number conform to the expected value after " +
                "valid followed by invalid performance creation");
        assertEquals(1, danceEv1.getPerformances().size(),"Number of performance of the event conform to the expected value " +
                "after valid followed by invalid performance creation");
    }

    @Test
    void createNullPerformanceValidPerformanceTest() {
        TicketedEvent danceEv1 = state.createTicketedEvent(provider1, "HipHopHoser",
                EventType.Dance, 15, 100);

        EventPerformance danceP2 = state.createEventPerformance(null, "testaddress",
                LocalDateTime.now().plusDays(15), LocalDateTime.now().plusDays(15).plusHours(4),
                List.of("michael jackson"), true, true, true,
                300, 400);

        EventPerformance danceP1 = state.createEventPerformance(danceEv1, "testaddress",
                LocalDateTime.now().plusDays(15), LocalDateTime.now().plusDays(15).plusHours(4),
                List.of("michael jackson"), true, true, true,
                300, 400);

        assertEquals(1, danceP1.getPerformanceNumber(),"Performance number conform to the expected value after " +
                "invalid followed by valid performance creation");
        assertEquals(1, danceEv1.getPerformances().size(),"Number of performance of the event conform to the expected value " +
                "after invalid followed by valid performance creation");
    }

    @Test
    void createPerformanceNullEventTest() {
        EventPerformance buskP2 = state.createEventPerformance(null, "1",
                LocalDateTime.now().plusDays(15), LocalDateTime.now().plusDays(16),
                List.of("pop smoke"), true, false, true,
                300, 400);

        assertNull(buskP2,"Null createEventPerformance result for null event input");
    }

    @Test
    void createPerformanceNullVenueTest() {
        NonTicketedEvent buskEv1 = state.createNonTicketedEvent(provider1, "buskIt",
                EventType.Music);

        EventPerformance buskP3 = state.createEventPerformance(buskEv1, null,
                LocalDateTime.now().plusDays(15), LocalDateTime.now().plusDays(16),
                List.of("pop smoke"), true, false, true,
                300, 400);

        assertNull(buskP3,"Null createEventPerformance result for null venueAddress input");
    }

    @Test
    void createPerformanceNullStartTimeTest() {
        NonTicketedEvent buskEv1 = state.createNonTicketedEvent(provider1, "buskIt", EventType.Music);

        EventPerformance buskP4 = state.createEventPerformance(buskEv1, "1",
                null, LocalDateTime.now().plusDays(16),
                List.of("pop smoke"), true, false, true,
                300, 400);

        assertNull(buskP4,"Null createEventPerformance result for null startDateTime input");
    }

    @Test
    void createPerformanceNullEndTimeTest() {
        NonTicketedEvent buskEv1 = state.createNonTicketedEvent(provider1, "buskIt",
                EventType.Music);

        EventPerformance buskP5 = state.createEventPerformance(buskEv1, "1",
                LocalDateTime.now().plusDays(15), null,
                List.of("pop smoke"), true, false, true,
                300, 400);

        assertNull(buskP5,"Null createEventPerformance result for null endDateTime input");
    }

    @Test
    void createPerformanceNullPerformersTest() {
        NonTicketedEvent buskEv1 = state.createNonTicketedEvent(provider1, "buskIt",
                EventType.Music);

        EventPerformance buskP6 = state.createEventPerformance(buskEv1, "1",
                LocalDateTime.now().plusDays(15), LocalDateTime.now().plusDays(16),
                null, true, false, true,
                300, 400);

        assertNull(buskP6,"Null createEventPerformance result for null performerName input");
    }

    @Test
    void createPerformanceBlankVenueTest() {
        NonTicketedEvent buskEv1 = state.createNonTicketedEvent(provider1, "buskIt",
                EventType.Music);

        EventPerformance buskP2 = state.createEventPerformance(buskEv1, " ",
                LocalDateTime.now().plusDays(15), LocalDateTime.now().plusDays(16),
                List.of("pop smoke"), true, false, true,
                300, 400);

        assertNull(buskP2,"Null createEventPerformance result for blank venueAddress input");
    }

    @Test
    void createPerformanceCapacity0Test() {
        NonTicketedEvent buskEv1 = state.createNonTicketedEvent(provider1, "buskIt",
                EventType.Music);

        EventPerformance buskP3 = state.createEventPerformance(buskEv1, "1",
                LocalDateTime.now().plusDays(15), LocalDateTime.now().plusDays(16),
                List.of("pop smoke"), true, false, true,
                0, 400);

        assertNull(buskP3,"Null createEventPerformance result for capacityLimit input equals 0");
    }

    @Test
    void createPerformanceNegativeCapacityTest() {
        NonTicketedEvent buskEv1 = state.createNonTicketedEvent(provider1, "buskIt",
                EventType.Music);

        EventPerformance buskP3 = state.createEventPerformance(buskEv1, "1",
                LocalDateTime.now().plusDays(15), LocalDateTime.now().plusDays(16),
                List.of("pop smoke"), true, false, true,
                -1, 400);

        assertNull(buskP3,"Null createEventPerformance result for negative capacityLimit input");
    }

    @Test
    void createPerformanceVenueSize0Test() {
        NonTicketedEvent buskEv1 = state.createNonTicketedEvent(provider1, "buskIt",
                EventType.Music);

        EventPerformance buskP4 = state.createEventPerformance(buskEv1, "1",
                LocalDateTime.now().plusDays(15), LocalDateTime.now().plusDays(16),
                List.of("pop smoke"), true, false, true,
                300, 0);

        assertNull(buskP4,"Null createEventPerformance result for venueSize input equals 0");
    }

    @Test
    void createPerformanceNegativeVenueSizeTest() {
        NonTicketedEvent buskEv1 = state.createNonTicketedEvent(provider1, "buskIt",
                EventType.Music);

        EventPerformance buskP4 = state.createEventPerformance(buskEv1, "1",
                LocalDateTime.now().plusDays(15), LocalDateTime.now().plusDays(16),
                List.of("pop smoke"), true, false, true,
                300, -1);

        assertNull(buskP4,"Null createEventPerformance result for negative venueSize input");
    }

    @Test
    void createPerformanceEndBeforeStartTest() {
        NonTicketedEvent buskEv1 = state.createNonTicketedEvent(provider1, "buskIt",
                EventType.Music);

        EventPerformance buskP5 = state.createEventPerformance(buskEv1, "1",
                LocalDateTime.now().plusDays(16), LocalDateTime.now().plusDays(15),
                List.of("pop smoke"), true, false, true,
                300, 400);

        assertNull(buskP5,"Null createEventPerformance result for performance endTime before the startTime");
    }

    @Test
    void createPerformanceEndsInThePast() {
        NonTicketedEvent buskEv1 = state.createNonTicketedEvent(provider1, "buskIt",
                EventType.Music);

        EventPerformance buskP5 = state.createEventPerformance(buskEv1, "1",
                LocalDateTime.now().minusDays(16), LocalDateTime.now().minusDays(16).plusHours(1),
                List.of("pop smoke"), true, false, true,
                300, 400);

        assertNull(buskP5,"Null createEventPerformance result for performance endTime before the current time");
    }

    @Test
    void nextPerformanceNumberTest() {
        TicketedEvent danceEv1 = state.createTicketedEvent(provider1, "HipHopHoser",
                EventType.Dance, 15, 100);
        NonTicketedEvent buskEv1 = state.createNonTicketedEvent(provider1, "buskIt",
                EventType.Music);

        EventPerformance danceP1 = state.createEventPerformance(danceEv1, "testaddress",
                LocalDateTime.now().plusDays(15), LocalDateTime.now().plusDays(15).plusHours(4),
                List.of("michael jackson"), true, true, true,
                300, 400);
        EventPerformance buskP1 = state.createEventPerformance(buskEv1, "testaddress2",
                LocalDateTime.now().plusDays(15), LocalDateTime.now().plusDays(16),
                List.of("pop smoke"), true, false, true,
                300, 400);

        assertEquals(danceP1.getPerformanceNumber() + 1, buskP1.getPerformanceNumber(),"The performance number after " +
                "two consecutive performance creation conform to the expected value");
    }

    @Test
    void nextEventNumberTest() {
        TicketedEvent danceEv1 = state.createTicketedEvent(provider1, "HipHopHoser",
                EventType.Dance, 15, 100);
        NonTicketedEvent buskEv1 = state.createNonTicketedEvent(provider1, "buskIt",
                EventType.Music);

        assertEquals(danceEv1.getEventNumber() + 1, buskEv1.getEventNumber(),"The event number after " +
                "two consecutive event creation conform to the expected value");
    }

    @Test
    void existsSameEventTest() {
        TicketedEvent danceEv1 = state.createTicketedEvent(provider2, "HipHopHoser", EventType.Dance,
                15, 100);
        NonTicketedEvent danceEv2 = state.createNonTicketedEvent(provider2, "HipHopHoser",
                EventType.Dance);

        assertNull(danceEv2,"Null createNonTicketedEvent result for event already existed");
        assertEquals(1, state.getAllEvents().size(),"Number of events in event state conform to the expected value after invalid event creation");
    }

    @Test
    void existsSamePerformanceTest() {
        TicketedEvent danceEv1 = state.createTicketedEvent(provider1, "HipHopHoser", EventType.Dance,
                15, 100);
        NonTicketedEvent buskEv1 = state.createNonTicketedEvent(provider2, "buskIt", EventType.Music);

        EventPerformance danceP1 = state.createEventPerformance(danceEv1, "testaddress",
                LocalDateTime.now().plusDays(15), LocalDateTime.now().plusDays(15).plusHours(4),
                List.of("michael jackson"), true, true, true,
                300, 400);

        EventPerformance buskP1 = state.createEventPerformance(buskEv1, "testaddress",
                LocalDateTime.now().plusDays(15).minusHours(1), LocalDateTime.now().plusDays(16),
                List.of("pop smoke"), true, false, true,
                300, 400);

        assertNull(buskP1,"Null createEventPerformance result for performance already existed");
    }

    @Test
    void findEventByNumberValidNumberTest() {
        NonTicketedEvent danceEv1 = state.createNonTicketedEvent(provider1, "buskIt",
                EventType.Music);

        Event result = state.findEventByNumber(danceEv1.getEventNumber());
        assertNotNull(result,"Not null for findEventByNumber result for passing a valid event number");
        assertEquals(danceEv1, result,"findEventByNumber result conform to the expected event");
    }

    @Test
    void findEventByNumberPlus1Test() {
        state.createNonTicketedEvent(provider1, "buskIt", EventType.Music);

        Event result = state.findEventByNumber(2);
        assertNull(result,"Null for findEventByNumber result for passing a non-existing event number");
    }

    @Test
    void findEventByNumber0Test() {
        state.createNonTicketedEvent(provider1, "buskIt", EventType.Music);

        Event result = state.findEventByNumber(0);
        assertNull(result,"Null for findEventByNumber result for passing a eventNumber equals 0");
    }

    @Test
    void findEventByNegativeNumberTest() {
        state.createNonTicketedEvent(provider1, "buskIt", EventType.Music);

        Event result = state.findEventByNumber(-1);
        assertNull(result,"Null for findEventByNumber result for passing a negative eventNumber");
    }

    @Test
    void getAllEvents() {
        TicketedEvent danceEv1 = state.createTicketedEvent(provider1, "HipHopHoser", EventType.Dance,
                15, 100);
        NonTicketedEvent buskEv1 = state.createNonTicketedEvent(provider2 , "buskIt",
                EventType.Music);

        List<Event> result = state.getAllEvents();

        assertEquals(2, result.size(),"Number of events in getAllEvents result conform to the expected value");
        assertEquals(danceEv1, result.get(0),"Event in getAllEvents result conform to the expected event");
        assertEquals(buskEv1, result.get(1),"Event in getAllEvents result conform to the expected event");
    }
}