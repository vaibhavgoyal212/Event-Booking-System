package command;

import controller.Context;
import java.util.*;

import controller.Controller;
import logging.LogStatus;
import logging.Logger;
import model.*;
import state.IBookingState;

public class GovernmentReport2Command implements ICommand {
    private final String orgName;
    private List<Consumer> result;

    /**
     * Initialises the command with the given organisation name
     * @param orgName the name of the organisation to find consumers from
     */
    public GovernmentReport2Command(String orgName) {
        this.orgName = orgName;
        result=Collections.<Consumer>emptyList();
    }

    /**
     * This method should not be called directly outside of testing. Normal usage is to create a
     * command object and execute it by passing to {@link  Controller#runCommand(ICommand)} instead.
     * @Specified by: {@link ICommand#execute(Context context)} in interface {@link ICommand}
     * @param context object that provides access to global application state
     */
    @Override
    public void execute(Context context) {
        User user = context.getUserState().getCurrentUser();

        if (!(user instanceof GovernmentRepresentative)) {
            Logger.getInstance().logAction("GovernmentReport2Command.execute()",
                    LogStatus.General.INVALID_USER);
            return;
        }

        EntertainmentProvider provider = getDesiredProvider(context);

        if (provider == null) {
            Logger.getInstance().logAction("GovernmentReport2Command.execute()",
                    LogStatus.GovernmentReport2LogStatus.NO_SUCH_ORGANISATION);
            return;
        }

        List<Event> activeEvents = getActiveTicketedEvents(provider);
        List<Booking> eventBookings = getEventBookings(activeEvents, context);
        List<Consumer> activeBookingUsers = getActiveBookingUsers(eventBookings);

        this.result = activeBookingUsers;
        Logger.getInstance().logAction("GovernmentReport2Command.execute()",
                LogStatus.GovernmentReport2LogStatus.GOVERNMENT_REPORT2_SUCCESS);
    }

    /**
     * Finds the entertainment provider with correct organisation name;
     * @param context object that provides access to global application state
     * @return the Entertainment Provider specified by the organisation name, null if it does not
     * exist
     */
    // Find the entertainment provider with the specified organisation name
    private EntertainmentProvider getDesiredProvider(Context context) {
        Collection<User> allCurrentUsers = context.getUserState().getAllUsers().values();
        for (User u : allCurrentUsers) {
            if (u instanceof EntertainmentProvider &&
                (((EntertainmentProvider) u).getOrgName().equals(orgName))) {
                return (EntertainmentProvider) u;
            }
        }
        return null;
    }

    /**
     * From a list of bookings, collect all the consumers who booked the active bookings in this
     * list
     * @param bookings a list of {@link Booking} objects
     * @return a List of unique {@link Consumer} objects who made the active bookings
     */
    private List<Consumer> getActiveBookingUsers(List<Booking> bookings) {
        Set<Consumer> activeBookingUsers = new HashSet<>();

        for (Booking b : bookings) {
            if (b.getStatus() == BookingStatus.ACTIVE) {
                activeBookingUsers.add(b.getBooker());
            }
        }

        return new ArrayList<Consumer>(activeBookingUsers);
    }

    /**
     * Compile a list of all the bookings made for every {@link Event} in a given list
     * @param events the list of events to retrieve bookings from
     * @param context object that provides access to global application state
     * @return a List of {@link Booking} objects made for all the events in the list
     */
    // For a list of events, retrieve all their bookings
    private List<Booking> getEventBookings(List<Event> events, Context context) {
        IBookingState bookingState = context.getBookingState();
        List<Booking> eventBookings = new ArrayList<>();
        for (Event e : events) {
            eventBookings.addAll(bookingState.findBookingsByEventNumber(e.getEventNumber()));
        }

        return eventBookings;
    }

    /**
     * Retrieves a list of all an {@link EntertainmentProvider}'s active events
     * @param provider an {@link EntertainmentProvider} to get events from
     * @return a List of active {@link Event} objects
     */
    // Get all active ticketed events hosted by a given provider
    private List<Event> getActiveTicketedEvents(EntertainmentProvider provider) {
        List<Event> activeTicketedEvents = new ArrayList<>();

        for (Event e : provider.getEvents()) {
            if (e.getStatus() == EventStatus.ACTIVE && (e instanceof TicketedEvent)) {
                activeTicketedEvents.add(e);
            }
        }

        return activeTicketedEvents;
    }

    /**
     * Get the result from the latest run of the command
     * @Specified by {@link ICommand#getResult} in interface {@link ICommand}
     * @return A List of unique {@link Consumer} objects if successful and null otherwise
     */
    @Override
    public List<Consumer> getResult() {
        return this.result;
    }
}
