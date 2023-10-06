package state;

import external.EntertainmentProviderSystem;
import logging.LogStatus;
import logging.Logger;
import model.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertNotNull;

public class EventState implements IEventState {
    private final List<Event> events;
    private long nextEventNumber;
    private long nextPerformanceNumber;

    public EventState() {
        this.events = new ArrayList<>();
        this.nextEventNumber = 1;
        this.nextPerformanceNumber = 1;
    }

    public EventState(IEventState other) {
        assertNotNull(other);

        this.events = new ArrayList<>();
        this.nextEventNumber = other.getAllEvents().size() + 1;
        this.nextPerformanceNumber = 0;

        List<Event> eventsList = other.getAllEvents();
        for (Event event : eventsList) {
            events.add(event);
            nextPerformanceNumber += event.getPerformances().size();
        }

        nextPerformanceNumber++;
    }

    public EventPerformance createEventPerformance(Event event, String venueAddress,
                                                   LocalDateTime startDateTime,
                                                   LocalDateTime endDateTime,
                                                   List<String> performerNames,
                                                   boolean hasSocialDistancing,
                                                   boolean hasAirFiltration, boolean isOutdoors,
                                                   int capacityLimit, int venueSize) {
        //Ensure no parameters are null or empty
        if (event == null || venueAddress == null || venueAddress.isBlank() || startDateTime == null
                || endDateTime == null || performerNames == null || capacityLimit < 1 ||
                venueSize < 1 || endDateTime.isBefore(startDateTime) || endDateTime.isBefore(LocalDateTime.now())) {
            Logger.getInstance().logAction("EventState.createEventPerformance()",
                    LogStatus.AddEventPerformanceLogStatus.ADD_PERFORMANCE_INVALID_FIELDS);
            return null;
        }

        //Create a new performance object and check an overlapping one does not already exist
        EventPerformance newPerformance = new EventPerformance(nextPerformanceNumber, event,
                venueAddress, startDateTime, endDateTime, performerNames, hasSocialDistancing,
                hasAirFiltration, isOutdoors, capacityLimit, venueSize);

        if (existsSamePerformance(newPerformance)) {
            Logger.getInstance().logAction("EventState.createEventPerformance()",
                    LogStatus.AddEventPerformanceLogStatus.ADD_PERFORMANCE_SAME_PERFORMANCE_EXIST);
            return null;
        }

        //Add the performance to the event and notify the relevant systems
        event.addPerformance(newPerformance);
        EntertainmentProviderSystem providerSystem = event.getOrganiser().getProviderSystem();
        providerSystem.recordNewPerformance(event.getEventNumber(), nextPerformanceNumber,
                startDateTime, endDateTime);

        nextPerformanceNumber++;
        return newPerformance;
    }

    //Checks if there exists a performance from another event at the same venue and at an
    // overlapping time with the new performance that is Active
    private boolean existsSamePerformance(EventPerformance newPerformance) {
        //Get all performances across the system
        List<EventPerformance> allPerformances = new ArrayList<>();
        for (Event e: this.events){
            if (e.getStatus().equals(EventStatus.ACTIVE)) {
                allPerformances.addAll(e.getPerformances());
            }
        }

        // Check that none of the events occur at the same venue and at an overlapping time
        for (EventPerformance performance : allPerformances) {
            if (performance.equals(newPerformance)) {
                return true;
            }

            LocalDateTime newStart = newPerformance.getStartDateTime();
            LocalDateTime newEnd = newPerformance.getEndDateTime();
            LocalDateTime oldStart = performance.getStartDateTime();
            LocalDateTime oldEnd = performance.getEndDateTime();

            if (performance.getVenueAddress().equals(newPerformance.getVenueAddress()) &&
                newStart.isBefore(oldEnd) && oldStart.isBefore(newEnd) &&
                performance.getEvent().getStatus() == EventStatus.ACTIVE) {
                return true;
            }
        }
        return false;
    }

    public NonTicketedEvent createNonTicketedEvent(EntertainmentProvider organiser, String title,
                                                   EventType type) {
        //Check input is valid
        if (organiser == null || title == null || title.isBlank() || type == null) {
            Logger.getInstance().logAction("EventState.createNonTicketedEvent()",
                    LogStatus.CreateNonTicketedEventLogStatus.EVENT_FIELDS_CANNOT_BE_EMPTY);
            return null;
        }

        //Create new event object and check that an identical one does not already exist
        NonTicketedEvent newEvent = new NonTicketedEvent(nextEventNumber, organiser, title, type);

        if (existsSameEvent(newEvent)) {
            Logger.getInstance().logAction("EventState.createNonTicketedEvent()",
                    LogStatus.CreateNonTicketedEventLogStatus.SAME_EVENT_EXISTS);
            return null;
        }

        events.add(newEvent);
        organiser.getProviderSystem().recordNewEvent(nextEventNumber, title, -1);
        organiser.addEvent(newEvent);

        nextEventNumber++;
        return newEvent;
    }

    public TicketedEvent createTicketedEvent(EntertainmentProvider organiser, String title,
                                             EventType type, double ticketPrice, int numTickets) {
        //Verify valid input
        if (organiser == null || title == null || title.isBlank() || type == null ||
            ticketPrice <= 0 || numTickets <= 0) {
            Logger.getInstance().logAction("EventState.createTicketedEvent()",
                    LogStatus.CreateTicketedEventLogStatus.INVALID_FIELDS);
            return null;
        }

        TicketedEvent newEvent =
                new TicketedEvent(nextEventNumber, organiser, title, type, ticketPrice, numTickets);

        if (existsSameEvent(newEvent)) {
            Logger.getInstance().logAction("EventState.createTicketedEvent()",
                    LogStatus.CreateTicketedEventLogStatus.SAME_EVENT_EXISTS);
            return null;
        }

        events.add(newEvent);
        organiser.getProviderSystem().recordNewEvent(nextEventNumber, title, numTickets);
        organiser.addEvent(newEvent);

        nextEventNumber++;
        return newEvent;
    }

    //Searches through all existing events to find one with the same organiser and title that
    // is still active and has at least one non-finished event;
    private boolean existsSameEvent(Event newEvent) {
        return this.events.stream().anyMatch( (Event e) ->
                e.getOrganiser().equals(newEvent.getOrganiser()) &&
                e.getTitle().equals(newEvent.getTitle()) &&
                e.getStatus() == EventStatus.ACTIVE &&
                e.getPerformances().stream().noneMatch( (EventPerformance p) ->
                        p.getEndDateTime().isBefore(LocalDateTime.now())));
    }

    public Event findEventByNumber(long eventNumber) {
        for (Event event : events) {
            if (event.getEventNumber() == eventNumber) {
                return event;
            }
        }
        return null;
    }

    public List<Event> getAllEvents() {
        return events;
    }
}
