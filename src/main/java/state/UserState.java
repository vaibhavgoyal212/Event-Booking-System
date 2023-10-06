package state;

import model.GovernmentRepresentative;
import model.User;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertNotNull;

public class UserState implements IUserState {
    private final Map<String, User> users;
    private User currentUser;

    public UserState() {
        users = new HashMap<>();
        currentUser = null;
        GovernmentRepresentative gov = new GovernmentRepresentative("margaret.thatcher@gov.uk", "The Good times  ", "paymentEmail@help.com");
        users.put(gov.getEmail(), gov);
    }

    public UserState(IUserState other) {
        assertNotNull(other);

        users = new HashMap<>();
        currentUser = other.getCurrentUser();

        Map<String, User> otherUsers = other.getAllUsers();
        for (String userEmail : other.getAllUsers().keySet()) {
            users.put(userEmail, otherUsers.get(userEmail));
        }
    }

    public void addUser(User user) {
        if (user != null) {
            users.put(user.getEmail(), user);
        }
    }

    public Map<String, User> getAllUsers() {
        return users;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(User user) {
        currentUser = user;
    }
}
