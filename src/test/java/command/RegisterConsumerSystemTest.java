package command;

import controller.Controller;
import logging.Logger;
import model.Consumer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class RegisterConsumerSystemTest {
    /*
     * TO TEST:
     * Test Register 1 consumer
     * Test Register multiple consumers
     * Test Register consumers with null name/email/phoneNumber/password/paymentAccountEmail
     * Test Register consumers the with same email address
     */

    @BeforeEach
    void printTestName(TestInfo testInfo) {
        System.out.println(testInfo.getDisplayName());
    }

    @AfterEach
    void clearLogs() {
        Logger.getInstance().clearLog();
        System.out.println("---");
    }

    private void logout(Controller controller) {
        controller.runCommand(new LogoutCommand());
    }

    @Test
    void registerOneConsumerTest() {
        Controller controller = new Controller();
        RegisterConsumerCommand regConsumerCmd = new RegisterConsumerCommand(
                "Consumer1",
                "consumer1email@personal.com",
                "4412345678900",
                "strongPassword1111",
                "consumer1email@payment.com"
        );

        controller.runCommand(regConsumerCmd);
        Consumer consumer = regConsumerCmd.getResult();
        assertEquals("Consumer1", consumer.getName(),"Consumer name in registration result " +
                "should conform to the input field");
        assertEquals("consumer1email@personal.com", consumer.getEmail(),"Consumer Email in registration result " +
                "should conform to the input field");
        assertEquals("consumer1email@payment.com", consumer.getPaymentAccountEmail(),"Consumer payment account email in registration result " +
                "should conform to the input field");
    }

    @Test
    void registerTwoConsumersWithoutLogoutTest() {
        Controller controller = new Controller();
        RegisterConsumerCommand regConsumerCmd = new RegisterConsumerCommand(
                "Consumer1",
                "consumer1email@personal.com",
                "4412345678900",
                "strongPassword1111",
                "consumer1email@payment.com"
        );
        controller.runCommand(regConsumerCmd);

        RegisterConsumerCommand regConsumerCmd2 = new RegisterConsumerCommand(
                "Consumer11",
                "consumer11email@personal.com",
                "44112345678900",
                "strongPassword11111",
                "consumer11email@payment.com"
        );
        controller.runCommand(regConsumerCmd2);

        assertNull(regConsumerCmd2.getResult(),"Null registration result for a user already logged in");
    }

    @Test
    void registerThreeConsumersTest() {
        Controller controller = new Controller();

        controller.runCommand(new RegisterConsumerCommand(
                "Consumer1",
                "consumer2email@personal.com",
                "4412345678900",
                "strongPassword1111",
                "consumer1email@payment.com"
        ));
        logout(controller);
        controller.runCommand(new RegisterConsumerCommand(
                "Consumer2",
                "consumer2.email@11.com",
                "4412345678901",
                "strongPassword2222",
                "consumer2.email@00.com"
        ));
        logout(controller);
        RegisterConsumerCommand regConsumerCmd = new RegisterConsumerCommand(
                "Consumer3",
                "consumer3email@personal.com",
                "4412345678902",
                "strongPassword3333",
                "consumer3email@payment.com"
        );
        logout(controller);

        controller.runCommand(regConsumerCmd);
        Consumer consumer = regConsumerCmd.getResult();
        assertEquals("Consumer3", consumer.getName(),"Consumer name in registration result " +
                "should conform to the input field");
        assertEquals("consumer3email@personal.com", consumer.getEmail(),"Consumer Email in registration result " +
                "should conform to the input field");
        assertEquals("consumer3email@payment.com", consumer.getPaymentAccountEmail(),"Consumer payment account email " +
                "in registration result should conform to the input field");
    }

    @Test
    void registerConsumerWithNullFieldsTest() {
        Controller controller = new Controller();

        RegisterConsumerCommand regConsumerCmd = new RegisterConsumerCommand(null, "con@p.com", "441234", "1234", "con@p.com");
        controller.runCommand(regConsumerCmd);
        assertNull(regConsumerCmd.getResult(),"Null registration result for registering command with null consumer name");

        regConsumerCmd = new RegisterConsumerCommand(
                "Not Null", null, "441234", "1234", "con@p.com");
        controller.runCommand(regConsumerCmd);
        assertNull(regConsumerCmd.getResult(),"Null registration result for registering command with null consumer email");

        regConsumerCmd = new RegisterConsumerCommand(
                "Not Null", "con@p.com", null, "1234", "con@p.com");
        controller.runCommand(regConsumerCmd);
        assertNull(regConsumerCmd.getResult(),"Null registration result for registering command with null consumer phoneNumber");

        regConsumerCmd = new RegisterConsumerCommand(
                "Not Null", "con@p.com", "441234", null, "con@p.com");
        controller.runCommand(regConsumerCmd);
        assertNull(regConsumerCmd.getResult(),"Null registration result for registering command with null consumer password");

        regConsumerCmd = new RegisterConsumerCommand(
                "Not Null", "con@p.com", "441234", "1234", null);
        controller.runCommand(regConsumerCmd);
        assertNull(regConsumerCmd.getResult(),"Null registration result for registering command with null consumer paymentAccountEmail");

        regConsumerCmd = new RegisterConsumerCommand(
                null, null, null, null, null);
        controller.runCommand(regConsumerCmd);
        assertNull(regConsumerCmd.getResult(),"Null registration result for registering command with all null fields");
    }

    @Test
    void registerConsumerWithEmptyFieldsTest() {
        Controller controller = new Controller();

        RegisterConsumerCommand regConsumerCmd = new RegisterConsumerCommand(" ", "con@p.com",
                "441234", "1234", "con@p.com");
        controller.runCommand(regConsumerCmd);
        assertNull(regConsumerCmd.getResult(),"Null registration result for registering command with empty consumer name");

        regConsumerCmd = new RegisterConsumerCommand(
                "Not Null", " ", "441234", "1234", "con@p.com");
        controller.runCommand(regConsumerCmd);
        assertNull(regConsumerCmd.getResult(),"Null registration result for registering command with empty consumer email");

        regConsumerCmd = new RegisterConsumerCommand(
                "Not Null", "con@p.com", " ", "1234", "con@p.com");
        controller.runCommand(regConsumerCmd);
        assertNull(regConsumerCmd.getResult(),"Null registration result for registering command with empty consumer phoneNumber");

        regConsumerCmd = new RegisterConsumerCommand(
                "Not Null", "con@p.com", "441234", " ", "con@p.com");
        controller.runCommand(regConsumerCmd);
        assertNull(regConsumerCmd.getResult(),"Null registration result for registering command with empty consumer password");

        regConsumerCmd = new RegisterConsumerCommand(
                "Not Null", "con@p.com", "441234", "1234", " ");
        controller.runCommand(regConsumerCmd);
        assertNull(regConsumerCmd.getResult(),"Null registration result for registering command with empty consumer paymentAccountEmail");
    }

    @Test
    void registerConsumersWithSameEmailTest() {
        Controller controller = new Controller();

        //Same email as Entertainment Provider
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
        logout(controller);

        RegisterConsumerCommand regConsumerCmd = new RegisterConsumerCommand(
                "Consumer0",
                "email@email.com",
                "2345678900",
                "strongPassword1111",
                "consumer1email@payment.com"
        );
        controller.runCommand(regConsumerCmd);
        logout(controller);
        assertNull(regConsumerCmd.getResult(),"Null registration result for registering consumer with existing EntertainmentProvider email");

        //Same as another consumer
        RegisterConsumerCommand regConsumer0Cmd = new RegisterConsumerCommand(
                "Consumer0",
                "nooneusemyemail@personal.com",
                "4412345678900",
                "strongPassword1111",
                "consumer0email@payment.com"
        );
        controller.runCommand(regConsumer0Cmd);
        logout(controller);

        regConsumerCmd = new RegisterConsumerCommand(
                "Consumer2",
                "nooneusemyemail@personal.com",
                "4412345678902",
                "strongPassword3333",
                "consumer2email@payment.com"
        );

        controller.runCommand(regConsumerCmd);
        assertNull(regConsumerCmd.getResult(),"Null registration result for registering consumer with existing consumer email");

        //Same as government
        regConsumerCmd = new RegisterConsumerCommand(
                "Consumer2",
                "margaret.thatcher@gov.uk",
                "4412345678902",
                "strongPassword3333",
                "consumer2email@payment.com"
        );

        controller.runCommand(regConsumerCmd);
        assertNull(regConsumerCmd.getResult(),"Null registration result for registering consumer with existing government email");
    }
}