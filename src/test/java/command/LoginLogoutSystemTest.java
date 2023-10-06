package command;

import controller.Controller;
import logging.Logger;
import model.*;
import model.User;
import org.junit.jupiter.api.*;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class LoginLogoutSystemTest {
    private static Controller controller = new Controller();

    private static void createUsers() {
        controller.runCommand(new RegisterConsumerCommand("gajodhar", "gajoocool@lik.com",
                "+447345678123", "gajosinghcool", "gajjopays@pay.com"));
        controller.runCommand(new LogoutCommand());

        controller.runCommand(new RegisterEntertainmentProviderCommand("SlickEntertains",
                "Sombrero Street", "SlickGetsMoney@usurper.com", "Josh Slicker",
                "joshSlickerino@gmail.com", "gotsToGetSlicked",
                new ArrayList<String>(List.of("SlickMan1", "SlickerSool")),
                new ArrayList<String>(List.of("Slick1Shout@gmail.com", "slicksoolshoot@gmail.com"))));
        controller.runCommand(new LogoutCommand());
    }

    private static User loginConsumer() {
        LoginCommand cmd = new LoginCommand("gajoocool@lik.com", "gajosinghcool");
        controller.runCommand(cmd);
        return cmd.getResult();
    }

    private static User loginConsumerNewLogin() {
        LoginCommand cmd = new LoginCommand("gajoocool2@lik.com", "whatever");
        controller.runCommand(cmd);
        return cmd.getResult();
    }

    private static void changeConsumerLogin() {
        UpdateConsumerProfileCommand cmd = new UpdateConsumerProfileCommand("gajosinghcool",
                "ar", "gajoocool2@lik.com", "11", "whatever",
                "heyo", new ConsumerPreferences());
        controller.runCommand(cmd);
    }

    private static User loginProvider() {
        LoginCommand cmd = new LoginCommand("joshSlickerino@gmail.com", "gotsToGetSlicked");
        controller.runCommand(cmd);
        return cmd.getResult();
    }

    private static User loginProviderNewLogin() {
        LoginCommand cmd = new LoginCommand("joshSlickerino2@gmail.com", "password");
        controller.runCommand(cmd);
        return cmd.getResult();
    }

    private static void changeProviderLogin() {
        UpdateEntertainmentProviderProfileCommand cmd = new UpdateEntertainmentProviderProfileCommand(
                "gotsToGetSlicked", "arg", "woot@lik.com",
                "11", "hithere", "joshSlickerino2@gmail.com",
                "password", new ArrayList<>(), new ArrayList<>());
        controller.runCommand(cmd);
    }

    private static User loginGovernment() {
        LoginCommand cmd = new LoginCommand("margaret.thatcher@gov.uk", "The Good times  ");
        controller.runCommand(cmd);
        return cmd.getResult();
    }

    private static Object logout() {
        LogoutCommand cmd = new LogoutCommand();
        controller.runCommand(cmd);
        return cmd.getResult();
    }

    @BeforeEach
    void printTestName(TestInfo testInfo) {
        System.out.println(testInfo.getDisplayName());
    }

    @BeforeEach
    void setupController() {
        controller = new Controller();
        createUsers();
    }

    @AfterEach
    void clearLogs() {
        Logger.getInstance().clearLog();
        System.out.println("---");
    }

    @Test
    void loginLogoutAllUsersTest() {
        User u = loginConsumer();
        logout();
        assertTrue(u instanceof Consumer,"Login consumer result should be instance of consumer class");

        u = loginProvider();
        logout();
        assertTrue(u instanceof EntertainmentProvider,"Login EntertainmentProvider result should be " +
                "instance of EntertainmentProvider class");

        u = loginGovernment();
        logout();
        assertTrue(u instanceof GovernmentRepresentative,"Login GovernmentRepresentative result should be " +
                "instance of GovernmentRepresentative class");
    }

    @Test
    void invalidLoginLogoutTest() {
        //logout with no user logged in
        Object o = logout();
        assertNull(o,"Null logout result for no user logged in");

        //login while another user is logged in
        loginConsumer();
        User u = loginProvider();
        assertNull(u,"Null login result for user already logged in");
        logout();

        //invalid login email
        LoginCommand cmd = new LoginCommand("ee", "www");
        controller.runCommand(cmd);
        assertNull(cmd.getResult(),"Null login result for invalid user login email");

        //invalid login password
        LoginCommand cmd2 = new LoginCommand("margaret.thatcher@gov.uk", "www");
        controller.runCommand(cmd2);
        assertNull(cmd2.getResult(),"Null login result for invalid user login password");

        //null login email
        LoginCommand cmd3 = new LoginCommand(null, "www");
        controller.runCommand(cmd3);
        assertNull(cmd3.getResult(),"Null login result for null user login email");

        //null login password
        LoginCommand cmd4 = new LoginCommand("margaret.thatcher@gov.uk", null);
        controller.runCommand(cmd4);
        assertNull(cmd4.getResult(),"Null login result for null user login password");

        //empty email
        LoginCommand cmd5 = new LoginCommand("  ", "password");
        controller.runCommand(cmd5);
        assertNull(cmd5.getResult(),"Null login result for empty user login email");

        //empty password
        LoginCommand cmd6 = new LoginCommand("margaret.thatcher@gov.uk", "  ");
        controller.runCommand(cmd6);
        assertNull(cmd6.getResult(),"Null login result for empty user login password");
    }

    @Test
    void changedUserProfileLoginLogout() {
        loginConsumer();
        changeConsumerLogin();
        logout();
        User u = loginConsumerNewLogin();
        assertTrue(u instanceof Consumer,"Login consumer result after consumer updated " +
                "login detail should be instance of consumer class");
        logout();

        //check that cannot login with old login info
        u = loginConsumer();
        assertNull(u,"Null login result for consumer login with old login info");

        loginProvider();
        changeProviderLogin();
        logout();
        u = loginProviderNewLogin();
        assertTrue(u instanceof EntertainmentProvider,"Login EntertainmentProvider result after EntertainmentProvider updated " +
                "login detail should be instance of EntertainmentProvider class");
        logout();

        u = loginProvider();
        assertNull(u,"Null login result for EntertainmentProvider login with old login info");
    }
}