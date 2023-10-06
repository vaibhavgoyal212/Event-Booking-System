package command;

import controller.Controller;
import logging.Logger;
import model.EntertainmentProvider;
import model.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

import java.util.ArrayList;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;


public class RegisterEntertainmentProviderRepresentativeSystemTest {
    private static EntertainmentProvider registerProvider1(Controller controller) {
        RegisterEntertainmentProviderCommand cmd = new RegisterEntertainmentProviderCommand(
                "Provider1",
                "Provider1Address",
                "Email@provider1.com",
                "Rep1",
                "Rep1@provider1.com",
                "IamRep1",
                new ArrayList<String>(Collections.singleton("RepWithFancyName1")),
                new ArrayList<String>(Collections.singleton("RepWithFancyName1@provider1.com"))
        );
        controller.runCommand(cmd);
        return cmd.getResult();
    }

    private static EntertainmentProvider registerProvider2(Controller controller) {
        RegisterEntertainmentProviderCommand cmd = new RegisterEntertainmentProviderCommand(
                "Provider2",
                "Provider2Address",
                "Email@provider2.com",
                "Rep2",
                "Rep2@provider2.com",
                "IamRep2",
                new ArrayList<String>(Collections.singleton("RepWithFancyName2")),
                new ArrayList<String>(Collections.singleton("RepWithFancyName2@provider2.com"))
        );
        controller.runCommand(cmd);
        return cmd.getResult();

    }

    private static User registerUserWithSameEmailAsRep2(Controller controller) {
        RegisterConsumerCommand cmd = new RegisterConsumerCommand(
                "User1",
                "Rep2@provider2.com",
                "+44123456789",
                "123",
                "User1@gmail.com"

        );
        controller.runCommand(cmd);
        return cmd.getResult();
    }

    private static EntertainmentProvider registerProvider5(Controller controller) {
        RegisterEntertainmentProviderCommand cmd = new RegisterEntertainmentProviderCommand(
                "Provider5",
                "Provider5Address",
                "Email@provider5.com",
                "Rep5",
                "Rep5@provider5.com",
                "IamRep5",
                new ArrayList<String>(Collections.singleton("RepWithFancyName5")),
                new ArrayList<String>(Collections.singleton("RepWithFancyName5@provider5.com"))
        );
        controller.runCommand(cmd);
        return cmd.getResult();

    }

    private static EntertainmentProvider registerProvider6WithSameOrgnameAndAddressAsProvider5(Controller controller) {
        RegisterEntertainmentProviderCommand cmd = new RegisterEntertainmentProviderCommand(
                "Provider5",
                "Provider5Address",
                "Email@provider6.com",
                "Rep6",
                "Rep6@provider6.com",
                "IamRep6",
                new ArrayList<String>(Collections.singleton("RepWithFancyName6")),
                new ArrayList<String>(Collections.singleton("RepWithFancyName6@provider6.com"))
        );
        controller.runCommand(cmd);
        return cmd.getResult();

    }

    private static void logout(Controller controller) {
        controller.runCommand(new LogoutCommand());
    }

    @BeforeEach
    void printTestName(TestInfo testInfo) {
        System.out.println(testInfo.getDisplayName());
    }

    @AfterEach
    void clearLogs() {
        Logger.getInstance().clearLog();
        System.out.println("---");
    }

    @Test
    void registerOneProviderTest() {
        Controller controller = new Controller();

        assertNotNull(registerProvider1(controller),"Not null registration result for registering a valid EntertainmentProvider");
    }

    @Test
    void registerTwoProvidersWithoutLogoutTest() {
        Controller controller = new Controller();
        registerProvider1(controller);

        assertNull(registerProvider2(controller),"Null registration result for a user already logged in");
    }

    @Test
    void registerThreeProvidersTest() {
        Controller controller = new Controller();

        assertNotNull(registerProvider1(controller),"Not null registration result for registering a valid EntertainmentProvider");
        logout(controller);

        assertNotNull(registerProvider2(controller),"Not null registration result for registering a valid EntertainmentProvider");
        logout(controller);

        assertNotNull(registerProvider5(controller),"Not null registration result for registering a valid EntertainmentProvider");
    }

    @Test
    void registerProviderInvalidFieldsTest() {
        Controller controller = new Controller();

        //null orgName
        RegisterEntertainmentProviderCommand cmd14 = new RegisterEntertainmentProviderCommand(
                null, "Provider1Address", "Email@provider1.com",
                "Rep1", "Rep1@provider1.com", "IamRep1",
                new ArrayList<String>(Collections.singleton("RepWithFancyName1")),
                new ArrayList<String>(Collections.singleton("RepWithFancyName1@provider1.com"))
        );
        controller.runCommand(cmd14);
        assertNull(cmd14.getResult(),"Null registration result for registering command with " +
                "null entertainmentProvider orgName");

        //null orgAddress
        RegisterEntertainmentProviderCommand cmd13 = new RegisterEntertainmentProviderCommand(
                "Provider1", null, "Email@provider1.com",
                "Rep1", "Rep1@provider1.com", "IamRep1",
                new ArrayList<String>(Collections.singleton("RepWithFancyName1")),
                new ArrayList<String>(Collections.singleton("RepWithFancyName1@provider1.com"))
        );
        controller.runCommand(cmd13);
        assertNull(cmd13.getResult(),"Null registration result for registering command with " +
                "null entertainmentProvider orgAddress");

        //null paymentAccountEmail
        RegisterEntertainmentProviderCommand cmd12 = new RegisterEntertainmentProviderCommand(
                "Provider1", "Provider1Address", null,
                "Rep1", "Rep1@provider1.com", "IamRep1",
                new ArrayList<String>(Collections.singleton("RepWithFancyName1")),
                new ArrayList<String>(Collections.singleton("RepWithFancyName1@provider1.com"))
        );
        controller.runCommand(cmd12);
        assertNull(cmd12.getResult(),"Null registration result for registering command with " +
                "null entertainmentProvider paymentAccountEmail");

        //null mainRepEmail
        RegisterEntertainmentProviderCommand cmd11 = new RegisterEntertainmentProviderCommand(
                "Provider1", "Provider1Address", "Email@provider1.com",
                null, "Rep1@provider1.com", "IamRep1",
                new ArrayList<String>(Collections.singleton("RepWithFancyName1")),
                new ArrayList<String>(Collections.singleton("RepWithFancyName1@provider1.com"))
        );
        controller.runCommand(cmd11);
        assertNull(cmd11.getResult(),"Null registration result for registering command with " +
                "null entertainmentProvider mainRepEmail");

        //null mainRepName
        RegisterEntertainmentProviderCommand cmd10 = new RegisterEntertainmentProviderCommand(
                "Provider1", "Provider1Address", "Email@provider1.com",
                null, "Rep1@provider1.com", "IamRep1",
                new ArrayList<String>(Collections.singleton("RepWithFancyName1")),
                new ArrayList<String>(Collections.singleton("RepWithFancyName1@provider1.com"))
        );
        controller.runCommand(cmd10);
        assertNull(cmd10.getResult(),"Null registration result for registering command with " +
                "null entertainmentProvider mainRepName");

        //null password
        RegisterEntertainmentProviderCommand cmd9 = new RegisterEntertainmentProviderCommand(
                "Provider1", "Provider1Address", "Email@provider1.com",
                "Rep1", "Rep1@provider1.com", null,
                new ArrayList<String>(Collections.singleton("RepWithFancyName1")),
                new ArrayList<String>(Collections.singleton("RepWithFancyName1@provider1.com"))
        );
        controller.runCommand(cmd9);
        assertNull(cmd9.getResult(),"Null registration result for registering command with " +
                "null password");

        //null otherRepNames
        RegisterEntertainmentProviderCommand cmd8 = new RegisterEntertainmentProviderCommand(
                "Provider1", "Provider1Address", "Email@provider1.com",
                "Rep1", "Rep1@provider1.com", "IamRep1",
                null,
                new ArrayList<String>(Collections.singleton("RepWithFancyName1@provider1.com"))
        );
        controller.runCommand(cmd8);
        assertNull(cmd8.getResult(),"Null registration result for registering command with " +
                "null entertainmentProvider otherRepNames");

        //null otherRepEmails
        RegisterEntertainmentProviderCommand cmd7 = new RegisterEntertainmentProviderCommand(
                "Provider1", "Provider1Address", "Email@provider1.com",
                "Rep1", "Rep1@provider1.com", "IamRep1",
                new ArrayList<>(Collections.singleton("RepWithFancyName1")),
                null
        );
        controller.runCommand(cmd7);
        assertNull(cmd7.getResult(),"Null registration result for registering command with " +
                "null entertainmentProvider otherRepEmails");

        //empty orgName
        RegisterEntertainmentProviderCommand cmd6 = new RegisterEntertainmentProviderCommand(
                " ", "Provider1Address", "Email@provider1.com",
                "Rep1", "Rep1@provider1.com", "IamRep1",
                new ArrayList<String>(Collections.singleton("RepWithFancyName1")),
                new ArrayList<String>(Collections.singleton("RepWithFancyName1@provider1.com"))
        );
        controller.runCommand(cmd6);
        assertNull(cmd6.getResult(),"Null registration result for registering command with " +
                "empty entertainmentProvider orgName");

        //empty ordAddress
        RegisterEntertainmentProviderCommand cmd5 = new RegisterEntertainmentProviderCommand(
                "Provider1", "  ", "Email@provider1.com",
                "Rep1", "Rep1@provider1.com", "IamRep1",
                new ArrayList<String>(Collections.singleton("RepWithFancyName1")),
                new ArrayList<String>(Collections.singleton("RepWithFancyName1@provider1.com"))
        );
        controller.runCommand(cmd5);
        assertNull(cmd5.getResult(),"Null registration result for registering command with " +
                "empty entertainmentProvider orgAddress");

        //empty paymentAccountEmail
        RegisterEntertainmentProviderCommand cmd4 = new RegisterEntertainmentProviderCommand(
                "Provider1", "Provider1Address", "  ",
                "Rep1", "Rep1@provider1.com", "IamRep1",
                new ArrayList<String>(Collections.singleton("RepWithFancyName1")),
                new ArrayList<String>(Collections.singleton("RepWithFancyName1@provider1.com"))
        );
        controller.runCommand(cmd4);
        assertNull(cmd4.getResult(),"Null registration result for registering command with " +
                "empty entertainmentProvider paymentAccountEmail");

        //empty mainRepEmail
        RegisterEntertainmentProviderCommand cmd3 = new RegisterEntertainmentProviderCommand(
                "Provider1", "Provider1Address", "Email@provider1.com",
                "Rep1", "  ", "IamRep1",
                new ArrayList<String>(Collections.singleton("RepWithFancyName1")),
                new ArrayList<String>(Collections.singleton("RepWithFancyName1@provider1.com"))
        );
        controller.runCommand(cmd3);
        assertNull(cmd3.getResult(),"Null registration result for registering command with " +
                "empty entertainmentProvider mainRepEmail");

        //empty mainRepName
        RegisterEntertainmentProviderCommand cmd2 = new RegisterEntertainmentProviderCommand(
                "Provider1", "Provider1Address", "Email@provider1.com",
                " ", "Rep1@provider1.com", "IamRep1",
                new ArrayList<String>(Collections.singleton("RepWithFancyName1")),
                new ArrayList<String>(Collections.singleton("RepWithFancyName1@provider1.com"))
        );
        controller.runCommand(cmd2);
        assertNull(cmd2.getResult(),"Null registration result for registering command with " +
                "empty entertainmentProvider mainRepName");

        //empty password
        RegisterEntertainmentProviderCommand cmd1 = new RegisterEntertainmentProviderCommand(
                "Provider1", "Provider1Address", "Email@provider1.com",
                "Rep1", "Rep1@provider1.com", " ",
                new ArrayList<String>(Collections.singleton("RepWithFancyName1")),
                new ArrayList<String>(Collections.singleton("RepWithFancyName1@provider1.com"))
        );
        controller.runCommand(cmd1);
        assertNull(cmd1.getResult(),"Null registration result for registering command with " +
                "empty entertainmentProvider password");
    }

    @Test
    void registerProvidersWithSameEmailTest() {
        Controller controller = new Controller();

        //Same email as Consumer
        RegisterConsumerCommand regConsumerCmd = new RegisterConsumerCommand(
                "Consumer0",
                "email@email.com",
                "2345678900",
                "strongPassword1111",
                "consumer1email@payment.com"
        );
        controller.runCommand(regConsumerCmd);
        logout(controller);

        RegisterEntertainmentProviderCommand providerCmd = new RegisterEntertainmentProviderCommand(
                "org1",
                "bad@personal.com",
                "345678900",
                "Bob",
                "email@email.com",
                "password",
                new ArrayList<>(),
                new ArrayList<>());
        controller.runCommand(providerCmd);
        assertNull(providerCmd.getResult(),"Null registration result for registering EntertainmentProvider " +
                "with existing consumer email");

        //Same as another entertainment provider
        RegisterEntertainmentProviderCommand providerCmd1 =
                new RegisterEntertainmentProviderCommand(
                "org1",
                "bad@personal.com",
                "345678900",
                "Bob",
                "email2@email.com",
                "password",
                new ArrayList<>(),
                new ArrayList<>());
        controller.runCommand(providerCmd1);
        logout(controller);

        RegisterEntertainmentProviderCommand providerCmd2 =
                new RegisterEntertainmentProviderCommand(
                        "org1",
                        "bad@personal.com",
                        "345678900",
                        "Bob",
                        "email2@email.com",
                        "password",
                        new ArrayList<>(),
                        new ArrayList<>());
        controller.runCommand(providerCmd1);
        assertNull(providerCmd.getResult(),"Null registration result for registering EntertainmentProvider " +
                "with existing EntertainmentProvider email");
    }
}
