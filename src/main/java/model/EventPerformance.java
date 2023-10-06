package model;

import java.time.LocalDateTime;
import java.util.List;

public class EventPerformance {
    private final long performanceNumber;
    private final Event event;
    private final String venueAddress;
    private final LocalDateTime startDateTime;
    private final LocalDateTime endDateTime;
    private final List<String> performerNames;
    private final boolean hasSocialDistancing;
    private final boolean hasAirFiltration;
    private final boolean isOutdoors;
    private final int capacityLimit;
    private final int venueSize;

    public EventPerformance(long performanceNumber, Event event, String venueAddress,
                            LocalDateTime startDateTime, LocalDateTime endDateTime,
                            List<String> performerNames, boolean hasSocialDistancing,
                            boolean hasAirFiltration, boolean isOutdoors,
                            int capacityLimit, int venueSize) {

        this.performanceNumber = performanceNumber;
        this.event = event;
        this.venueAddress = venueAddress;
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
        this.performerNames = performerNames;
        this.hasSocialDistancing = hasSocialDistancing;
        this.hasAirFiltration = hasAirFiltration;
        this.isOutdoors = isOutdoors;
        this.capacityLimit = capacityLimit;
        this.venueSize = venueSize;
    }

    public long getPerformanceNumber() {
        return this.performanceNumber;
    }

    public Event getEvent() {
        return this.event;
    }

    public LocalDateTime getStartDateTime() {
        return this.startDateTime;
    }

    public LocalDateTime getEndDateTime() {
        return this.endDateTime;
    }

    public boolean hasSocialDistancing() {
        return this.hasSocialDistancing;
    }

    public boolean hasAirFiltration() {
        return this.hasAirFiltration;
    }

    public boolean isOutdoors() {
        return this.isOutdoors;
    }

    public int getCapacityLimit() {
        return this.capacityLimit;
    }

    public int getVenueSize() {
        return this.venueSize;
    }

    public String getVenueAddress() {
        return this.venueAddress;
    }

    @Override
    public String toString() {
        return "EventPerformance{" +
                "performanceNumber=" + performanceNumber +
                ", event=" + event +
                ", venueAddress='" + venueAddress + '\'' +
                ", startDateTime=" + startDateTime +
                ", endDateTime=" + endDateTime +
                ", performerNames=" + performerNames +
                ", hasSocialDistancing=" + hasSocialDistancing +
                ", hasAirFiltration=" + hasAirFiltration +
                ", isOutdoors=" + isOutdoors +
                ", capacityLimit=" + capacityLimit +
                ", venueSize=" + venueSize +
                '}';
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (!(other instanceof EventPerformance)) {
            return false;
        }

        EventPerformance otherPerformance = (EventPerformance) other;

        return this.event.equals(otherPerformance.getEvent()) &&
                this.venueAddress.equals(otherPerformance.getVenueAddress()) &&
                this.startDateTime.equals(otherPerformance.getStartDateTime()) &&
                this.endDateTime.equals(otherPerformance.getEndDateTime());
    }
}
