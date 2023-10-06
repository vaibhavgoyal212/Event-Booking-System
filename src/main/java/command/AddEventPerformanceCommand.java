package command;

import controller.Context;
import java.time.LocalDateTime;
import java.util.List;
import logging.LogStatus;
import logging.Logger;
import model.EntertainmentProvider;
import model.Event;
import model.EventPerformance;
import model.User;

public class AddEventPerformanceCommand implements ICommand {
    private final long eventNumber;
    private final String venueAddress;
    private final LocalDateTime startDateTime;
    private final LocalDateTime endDateTime;
    private final List<String> performerNames;
    private final boolean hasSocialDistancing;
    private final boolean hasAirFiltration;
    private final boolean isOutdoors;
    private final int capacityLimit;
    private final int venueSize;
    private EventPerformance performance;

    public AddEventPerformanceCommand(long eventNumber, String venueAddress,
                                      LocalDateTime startDateTime, LocalDateTime endDateTime,
                                      List<String> performerNames, boolean hasSocialDistancing,
                                      boolean hasAirFiltration, boolean isOutdoors, int capacityLimit,
                                      int venueSize) {

        this.eventNumber = eventNumber;
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

    @Override
    public void execute(Context context) {
        User user = context.getUserState().getCurrentUser();
        Event event = context.getEventState().findEventByNumber(eventNumber);


        //Verify user is logged in, the event exists, and the organiser of the event
        if (user == null) {
            Logger.getInstance().logAction("AddEventPerformanceCommand.execute()",
                    LogStatus.General.USER_NOT_LOGGED_IN);
            return;
        }
        if (!(user instanceof EntertainmentProvider)) {
            Logger.getInstance().logAction("AddEventPerformanceCommand.execute()",
                    LogStatus.General.NOT_ENTERTAINMENT_PROVIDER);
            return;
        }
        if (event == null) {
            Logger.getInstance().logAction("AddEventPerformanceCommand.execute()",
                    LogStatus.General.EVENT_NOT_FOUND);
            return;
        }
        if (user != event.getOrganiser()) {
            Logger.getInstance().logAction("AddEventPerformanceCommand.execute()",
                    LogStatus.General.NOT_EVENT_ORGANISER);
            return;
        }

        //Add a new performance to the event state
        performance = context.getEventState()
                .createEventPerformance(event, venueAddress, startDateTime, endDateTime, performerNames,
                        hasSocialDistancing, hasAirFiltration, isOutdoors, capacityLimit, venueSize);

        Logger.getInstance().logAction("AddEventPerformanceCommand.execute()",
                LogStatus.AddEventPerformanceLogStatus.ADD_PERFORMANCE_SUCCESS);
    }

    @Override
    public EventPerformance getResult() {
        return performance;
    }
}
