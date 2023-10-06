package state;

import model.*;

import java.time.LocalDateTime;
import java.util.List;

public interface IEventState {
    EventPerformance createEventPerformance(Event event, String venueAddress,
                                            LocalDateTime startDateTime, LocalDateTime endDateTime,
                                            List<String> performerNames, boolean hasSocialDistancing,
                                            boolean hasAirFiltration, boolean isOutdoors,
                                            int capacityLimit, int venueSize);

    NonTicketedEvent createNonTicketedEvent(EntertainmentProvider organiser, String title,
                                            EventType type);

    TicketedEvent createTicketedEvent(EntertainmentProvider organiser, String title, EventType type,
                                      double ticketPrice, int numTickets);

    Event findEventByNumber(long eventNumber);

    List<Event> getAllEvents();
}
