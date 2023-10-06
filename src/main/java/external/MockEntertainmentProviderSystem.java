package external;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class MockEntertainmentProviderSystem implements EntertainmentProviderSystem {
    private final String orgName;
    private final String orgAddress;
    private final Map<Long, LocalEvent> events;
    private final Map<Long, LocalBooking> bookings;

    public MockEntertainmentProviderSystem(String orgName, String orgAddress) {
        this.orgName = orgName;
        this.orgAddress = orgAddress;
        this.events = new HashMap<>();
        this.bookings = new HashMap<>();
    }

    public void recordNewEvent(long eventNumber, String title, int numTickets) {
        events.put(eventNumber, new LocalEvent(eventNumber, title, numTickets));
    }

    public void cancelEvent(long eventNumber, String message) {
        events.get(eventNumber).active = false;
    }

    public void recordNewPerformance(long eventNumber, long performanceNumber,
                                     LocalDateTime startDateTime, LocalDateTime endDateTime) {
        LocalEvent event = events.get(eventNumber);
        LocalPerformance newPerformance = new LocalPerformance(performanceNumber,
                eventNumber, startDateTime, endDateTime);
        event.performances.put(performanceNumber, newPerformance);
    }

    public int getNumTicketsLeft(long eventNumber, long performanceNumber) {
        return events.get(eventNumber).numTickets;
    }

    public void recordNewBooking(long eventNumber, long performanceNumber, long bookingNumber,
                                 String consumerName, String consumerEmail, int bookedTickets) {
        LocalBooking booking = new LocalBooking(performanceNumber, eventNumber, bookingNumber,
                consumerName, consumerEmail, bookedTickets);
        events.get(eventNumber).numTickets -= bookedTickets;
        bookings.put(bookingNumber, booking);
    }

    public void cancelBooking(long bookingNumber) {
        LocalBooking booking = bookings.get(bookingNumber);
        events.get(booking.eventId).numTickets += booking.bookedTickets;
    }

    public void recordSponsorshipAcceptance(long eventNumber, int sponsoredPricePercent) {
        events.get(eventNumber).sponsoredPricePercentage = sponsoredPricePercent;
    }

    public void recordSponsorshipRejection(long eventNumber) {
        events.get(eventNumber).sponsoredPricePercentage = 0;
    }

    private class LocalEvent {
        public final long eventId;
        public final String eventName;
        public int numTickets;
        public int sponsoredPricePercentage;
        public final Map<Long, LocalPerformance> performances;
        public boolean active;

        public LocalEvent(long eventId, String eventName, int numTickets) {
            this.eventId = eventId;
            this.eventName = eventName;
            this.numTickets = numTickets;
            this.performances = new HashMap<>();
            this.sponsoredPricePercentage = -1;
            this.active = true;
        }
    }

    private static class LocalPerformance {
        public final long performanceId;
        public final long eventId;
        public final LocalDateTime startDateTime;
        public final LocalDateTime endDateTime;

        public LocalPerformance(long performanceId, long eventId, LocalDateTime startDateTime,
                                LocalDateTime endDateTime) {
            this.performanceId = performanceId;
            this.startDateTime = startDateTime;
            this.endDateTime = endDateTime;
            this.eventId = eventId;
        }
    }

    private static class LocalBooking {
        public final long performanceId;
        public final long eventId;
        public final long bookingNumber;
        public final String consumerName;
        public final String consumerEmail;
        public final int bookedTickets;

        public LocalBooking(long performanceId, long eventId, long bookingNumber,
                            String consumerName, String consumerEmail, int bookedTickets) {
            this.performanceId = performanceId;
            this.eventId = eventId;
            this.bookingNumber = bookingNumber;
            this.consumerName = consumerName;
            this.consumerEmail = consumerEmail;
            this.bookedTickets = bookedTickets;
        }
    }
}
