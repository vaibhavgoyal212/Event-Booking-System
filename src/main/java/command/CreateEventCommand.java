package command;

import controller.Context;
import model.EntertainmentProvider;
import model.EventType;
import model.User;

public abstract class CreateEventCommand implements ICommand {
    protected Long eventNumberResult;
    protected final String title;
    protected final EventType type;

    public CreateEventCommand(String title, EventType type) {
        this.title = title;
        this.type = type;
        this.eventNumberResult = null;
    }

    public Long getResult() {
        // Returns event number corresponding to the created event if successful and null otherwise
        return eventNumberResult;
    }

    protected boolean isUserAllowedToCreateEvent(Context context) {
        // Logged in and is Entertainment Provider
        User user = context.getUserState().getCurrentUser();
        return user instanceof EntertainmentProvider;
    }
}
