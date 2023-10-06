package command;

import controller.Context;
import external.EntertainmentProviderSystem;
import logging.LogStatus;
import logging.Logger;
import model.Event;
import model.TicketedEvent;

public class GetAvailablePerformanceTicketsCommand implements ICommand {
    private final long eventNumber;
    private final long performanceNumber;
    private Integer ticketNumber;

    public GetAvailablePerformanceTicketsCommand(long eventNumber, long performanceNumber) {
        this.eventNumber = eventNumber;
        this.performanceNumber = performanceNumber;
    }

    @Override
    public void execute(Context context) {
        Event event = context.getEventState().findEventByNumber(eventNumber);

        if (!(event instanceof TicketedEvent)) {
            Logger.getInstance().logAction("GetAvailablePerformanceTicketsCommand.execute()",
                    LogStatus.GetAvailablePerformanceTicketsLogStatus.NOT_TICKETED_EVENT);
            return;
        }

        TicketedEvent ticketedEvent = (TicketedEvent) event;

        //invalid performance
        if (ticketedEvent.getPerformanceByNumber(performanceNumber) == null) {
            Logger.getInstance().logAction("GetAvailablePerformanceTicketsCommand.execute()",
                    LogStatus.GetAvailablePerformanceTicketsLogStatus.PERFORMANCE_NOT_FOUND);
            return;
        }

        EntertainmentProviderSystem providerSystem = ticketedEvent.getOrganiser().getProviderSystem();
        ticketNumber = providerSystem.getNumTicketsLeft(eventNumber, performanceNumber);

        Logger.getInstance().logAction("GetAvailablePerformanceTicketsCommand.execute()",
                LogStatus.GetAvailablePerformanceTicketsLogStatus.SUCCESS);
    }

    @Override
    public Integer getResult() {
        return ticketNumber;
    }
}
