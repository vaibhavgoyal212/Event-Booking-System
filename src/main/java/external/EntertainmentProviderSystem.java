package external;

import java.time.LocalDateTime;

public interface EntertainmentProviderSystem {

    void cancelBooking(long bookingNumber);

    void cancelEvent(long eventNumber, String message);

    int getNumTicketsLeft(long eventNumber, long performanceNumber);

    void recordNewBooking(long eventNumber, long performanceNumber, long bookingNumber,
                          String consumerName, String consumerEmail, int bookedTickets);

    void recordNewEvent(long eventNumber, String title, int numTickets);

    void recordNewPerformance(long eventNumber, long performanceNumber,
                              LocalDateTime startDateTime, LocalDateTime endDateTime);

    void recordSponsorshipAcceptance(long eventNumber, int sponsoredPricePercent);

    void recordSponsorshipRejection(long eventNumber);
}
