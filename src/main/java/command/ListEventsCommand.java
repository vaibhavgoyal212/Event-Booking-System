package command;

import controller.Context;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import logging.LogStatus;
import logging.Logger;
import model.*;

public class ListEventsCommand implements ICommand {
    private final boolean userEventsOnly;
    private final boolean activeEventsOnly;
    private List<Event> finalEvents;

    public ListEventsCommand(boolean userEventsOnly, boolean activeEventsOnly) {
        this.userEventsOnly = userEventsOnly;
        this.activeEventsOnly = activeEventsOnly;
        this.finalEvents = null;
    }

    @Override
    public void execute(Context context) {
        User user = context.getUserState().getCurrentUser();

        //to filter events by user profile, must be logged in as a consumer of entertainment
        // provider
        if (userEventsOnly && (user == null || user instanceof GovernmentRepresentative)) {
            Logger.getInstance().logAction("ListEventsCommand.execute()",
                    LogStatus.ListEventsLogStatus.LIST_EVENTS_INVALID_USER);
            return;
        }

        // search for events that meet the given criteria
        finalEvents = new ArrayList<>();
        for (Event e : context.getEventState().getAllEvents()) {
            // check this event has at least one performance that has not yet begun
            boolean hasFuturePerformance = e.getPerformances().stream().anyMatch(
                        (EventPerformance p) -> LocalDateTime.now().isBefore(p.getStartDateTime())
                    );

            // if all performances have already begun, don't add this event to the result
            if (!hasFuturePerformance) {
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
                    if (!eventSatisfiesPreferences(pref, e)) {
                        continue;
                    }
                }
            }

            // the event fits all the criteria so add it to the result array
            finalEvents.add(e);
        }

        //no events were found
        if (finalEvents.size() == 0) {
            finalEvents = null;
        }

        Logger.getInstance().logAction("ListEventsCommand.execute()",
                LogStatus.ListEventsLogStatus.LIST_USER_EVENTS_SUCCESS);
    }

    // Check that at least one of this event's performances matches the consumer's profile and is
    // not in the past
    private boolean eventSatisfiesPreferences(ConsumerPreferences pref, Event e) {
        boolean foundMatch = false;
        for (EventPerformance p : e.getPerformances()) {
            foundMatch = foundMatch || ((p.hasAirFiltration() || !pref.preferAirFiltration)
                    && (p.hasSocialDistancing() || !pref.preferSocialDistancing)
                    && (p.isOutdoors() || !pref.preferOutdoorsOnly)
                    && (p.getCapacityLimit() <= pref.preferredMaxCapacity)
                    && (p.getVenueSize() <= pref.preferredMaxVenueSize)
                    && (p.getStartDateTime().isAfter(LocalDateTime.now())));
        }

        return foundMatch;
    }

    @Override
    public List<Event> getResult() {
        return this.finalEvents;
    }
}
