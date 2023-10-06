package model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public abstract class Event {
    protected final long eventNumber;
    protected final EntertainmentProvider organiser;
    protected final String title;
    protected final EventType type;
    protected EventStatus status;
    protected final List<EventPerformance> performances;

    protected Event(long eventNumber, EntertainmentProvider organiser, String title, EventType type) {
        this.eventNumber = eventNumber;
        this.organiser = organiser;
        this.title = title;
        this.type = type;
        status = EventStatus.ACTIVE;
        performances = new ArrayList<>();
    }

    public long getEventNumber() {
        return eventNumber;
    }

    public EntertainmentProvider getOrganiser() {
        return organiser;
    }

    public String getTitle() {
        return title;
    }

    public EventType getType() {
        return type;
    }

    public EventStatus getStatus() {
        return status;
    }

    public void cancel() {
        this.status = EventStatus.CANCELLED;
    }

    public void addPerformance(EventPerformance performance) {
        performances.add(performance);
    }

    public EventPerformance getPerformanceByNumber(long performanceNumber) {
        for (EventPerformance performance : performances) {
            if (performance.getPerformanceNumber() == performanceNumber) {
                return performance;
            }
        }
        return null;
    }

    public Collection<EventPerformance> getPerformances() {
        return performances;
    }

    @Override
    public boolean equals(Object other) {
        return other == this;
    }
}
