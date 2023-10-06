package state;

import model.Consumer;
import model.EntertainmentProvider;
import model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class UserStateTest {
    UserState state = new UserState();

    Consumer newConsumer = new Consumer("Bob", "bob@bob.com", "9671111",
            "safe", "money@money.com");
    EntertainmentProvider newProvider = new EntertainmentProvider("Bob",
            "bob's house", "bob@money.com", "Bob",
            "bob2@bob.com", "password", new ArrayList<>(), new ArrayList<>());

    @BeforeEach
    void resetState() {
        state = new UserState();
    }

    @Test
    void createFreshStateTest() {
        assertNull(state.getCurrentUser(),"Null currentUser for initialised state");
        assertEquals(1, state.getAllUsers().size(),"Number of users in freshly initialised state conform to the expected value");
        assertNotNull(state.getAllUsers().get("margaret.thatcher@gov.uk"),"Freshly initialised user state contains Government Representative");
    }

    @Test
    void createStateFromOtherTest() {
        UserState oldUserState = new UserState();
        oldUserState.addUser(newConsumer);
        oldUserState.addUser(newProvider);
        oldUserState.setCurrentUser(newConsumer);

        UserState newUserState = new UserState(oldUserState);

        assertEquals(oldUserState.getCurrentUser(), newUserState.getCurrentUser(),"CurrentUser in old user state corresponds to new user state");
        assertEquals(3, newUserState.getAllUsers().size(),"Number of users in new state conforms to the expected value");
    }

    @Test
    void addUserNullInputTest() {
        state.addUser(null);

        assertEquals(1, state.getAllUsers().size(),"Number of users in user state conforms to the expected value after adding null user");
    }

    @Test
    void addUserValidConsumerTest() {
        state.addUser(newConsumer);

        assertEquals(2, state.getAllUsers().size(),"Number of users in user state conforms to the expected value after adding valid consumer");
        assertNotNull(state.getAllUsers().get("bob@bob.com"),"User state contains the newly added consumer");
    }

    @Test
    void addUserValidEntertainmentProviderTest() {
        state.addUser(newProvider);

        assertEquals(2, state.getAllUsers().size(),"Number of users in user state conforms to the expected value after adding valid entertainment provider");
        assertNotNull(state.getAllUsers().get("bob2@bob.com"),"User state contains the newly added entertainment provider");
    }

    @Test
    void addUserSameUserTwiceTest() {
        state.addUser(newConsumer);
        state.addUser(newConsumer);

        assertEquals(2, state.getAllUsers().size(),"Number of users in user state conforms to the expected value after adding same consumer twice");
        assertNotNull(state.getAllUsers().get("bob@bob.com"),"User state contains the consumer after adding the same consumer twice");
    }

    @Test
    void getAllUsersOnlyGovernmentRepTest() {
        UserState userState = new UserState();

        Map<String, User> users = userState.getAllUsers();

        assertEquals(1, users.size(),"Number of users in getAllUsers result conform to the expected value");
    }

    @Test
    void getAllUsersMultipleUsersTest() {
        state.addUser(newConsumer);
        state.addUser(newProvider);
        Map<String, User> users = state.getAllUsers();

        assertEquals(3, users.size(),"Number of users in getAllUsers result conform to the expected value");
    }

    @Test
    void getCurrentUserLoggedOutTest() {
        User currUser = state.getCurrentUser();

        assertNull(currUser,"getCurrentUser result conform to the expected value");
    }

    @Test
    void getCurrentUserLoggedInTest() {
        state.setCurrentUser(newConsumer);

        User currUser = state.getCurrentUser();

        assertEquals(newConsumer, currUser,"getCurrentUser result conform to the expected value");
    }

    @Test
    void setCurrentUserNullTest() {
        state.setCurrentUser(null);

        User currUser = state.getCurrentUser();

        assertNull(currUser,"setCurrentUser sets the current user correctly");
    }

    @Test
    void setCurrentUserFromNotNullTest() {
        state.setCurrentUser(newConsumer);

        state.setCurrentUser(newProvider);

        assertEquals(newProvider, state.getCurrentUser(),"setCurrentUser sets the current user correctly after two consecutive valid user input");
    }

    @Test
    void replaceCurrentUserFromNullTest() {
        state.setCurrentUser(null);
        state.setCurrentUser(newConsumer);

        assertEquals(newConsumer, state.getCurrentUser(),"setCurrentUser sets the current user correctly after invalid followed by valid user input");
    }
}