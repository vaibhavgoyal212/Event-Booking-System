package command;

import controller.Context;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import logging.LogStatus;
import logging.Logger;
import model.*;

public class ListEventsOnGivenDateCommand implements ICommand {
    private final boolean userEventsOnly;
    private final boolean activeEventsOnly;
    private final LocalDateTime searchDateTime;
    private List<Event> eventsListed;

    public ListEventsOnGivenDateCommand(boolean userEventsOnly, boolean activeEventsOnly, LocalDateTime searchDateTime) {
        this.userEventsOnly = userEventsOnly;
        this.activeEventsOnly = activeEventsOnly;
        this.searchDateTime = searchDateTime;
        this.eventsListed = null;
    }

    @Override
    public void execute(Context context) {
        User user = context.getUserState().getCurrentUser();

        //to filter events by user profile, must be logged in as a consumer of entertainment
        // provider
        if (userEventsOnly && (user == null || user instanceof GovernmentRepresentative)) {
            Logger.getInstance().logAction("ListEventsOnGivenDateCommand.execute()",
                    LogStatus.General.INVALID_USER);
            return;
        }

        // make sure date search is valid
        if (searchDateTime == null || searchDateTime.isBefore(LocalDateTime.now())) {
            Logger.getInstance().logAction("ListEventsOnGivenDateCommand.execute()",
                    LogStatus.ListEventsOnGivenDateLogStatus.INVALID_SEARCH_DATE);
            return;
        }

        LocalDateTime searchDay = searchDateTime.truncatedTo(ChronoUnit.DAYS);

        // search for events that meet the given criteria
        eventsListed = new ArrayList<>();
        for (Event e : context.getEventState().getAllEvents()) {
            // check this event has at least one performance is within TargetWindow
            boolean hasMatchingPerformanceDate = e.getPerformances().stream().anyMatch(
                    (EventPerformance p) -> withinTimeWindow(p, searchDay)
            );

            // if all performances have already begun, don't add this event to the result
            if (!hasMatchingPerformanceDate) {
                continue;
            }

            // if search requests only active events, don't add inactive events to the result
            if (activeEventsOnly && (e.getStatus() != EventStatus.ACTIVE)) {
                continue;
            }

            // make sure the event meets the user's profile if necessary
            if (userEventsOnly) {
                // only keep events created by this provider
                if ((user instanceof EntertainmentProvider) && (e.getOrganiser() != user)) {
                    continue;
                } else if (user instanceof Consumer) {
                    // check that at least one performance matches the consumer's preferences
                    ConsumerPreferences pref = ((Consumer) user).getPreferences();
                    if (!eventSatisfiesPreferences(pref, e, searchDay)) {
                        continue;
                    }
                }
            }

            // the event fits all the criteria so add it to the result array
            eventsListed.add(e);
        }

        //no events were found
        if (eventsListed.size() == 0) {
            eventsListed = null;
        }

        Logger.getInstance().logAction("ListEventsOnGivenDateCommand.execute()",
                LogStatus.ListEventsOnGivenDateLogStatus.LIST_USER_EVENTS_FOUND_EVENTS_ON_DATE);
    }

    // Checks if there is at least one performance that matches all the consumer's preferences
    // and occurs during the specified day
    private boolean eventSatisfiesPreferences(ConsumerPreferences pref, Event e,
                                              LocalDateTime searchDay) {
        boolean foundMatch = false;
        for (EventPerformance p : e.getPerformances()) {
            foundMatch = foundMatch || ((p.hasAirFiltration() || !pref.preferAirFiltration)
                    && (p.hasSocialDistancing() || !pref.preferSocialDistancing)
                    && (p.isOutdoors() || !pref.preferOutdoorsOnly)
                    && (p.getCapacityLimit() <= pref.preferredMaxCapacity)
                    && (p.getVenueSize() <= pref.preferredMaxVenueSize)
                    && (p.getEndDateTime().isAfter(LocalDateTime.now()))
                    && withinTimeWindow(p, searchDay));
        }
        return foundMatch;
    }

    //Checks if a performance will overlap with the specified search day
    //The event can either start or end during that day or start before and end after that day
    private boolean withinTimeWindow(EventPerformance p, LocalDateTime searchDay) {
        return (searchDay.isEqual(p.getStartDateTime().truncatedTo(ChronoUnit.DAYS)) ||
                searchDay.isEqual(p.getEndDateTime().truncatedTo(ChronoUnit.DAYS)) ||
                (searchDay.isAfter(p.getStartDateTime().truncatedTo(ChronoUnit.DAYS)) &&
                        searchDay.isBefore(p.getEndDateTime().truncatedTo(ChronoUnit.DAYS))));
    }

    @Override
    public List<Event> getResult() {
        return eventsListed;
    }
}
