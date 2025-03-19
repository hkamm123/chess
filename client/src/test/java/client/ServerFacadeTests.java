package client;

import org.junit.jupiter.api.*;
import server.RegisterRequest;
import server.RegisterResult;
import server.Server;

import static dataaccess.UserDao.USER_TAKEN_ERR_MSG;
import static org.junit.jupiter.api.Assertions.*;


public class ServerFacadeTests {

    private static Server server;
    private static ServerFacade serverFacade;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        serverFacade = new ServerFacade("http://localhost:" + port);
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }

    @BeforeEach
    public void setup() {
        serverFacade.clear();
    }

    @Test
    public void successRegister() {
        RegisterRequest req = new RegisterRequest("username", "password", "email");
        RegisterResult result = serverFacade.register(req);
        assertNull(result.message(), "result message was not null");
        assertTrue(
                result.authToken().length() > 10,
                "authToken was not created or not sufficiently long"
        );
        assertEquals("username", result.username());
    }

    @Test
    public void registerDuplicateUsernameGives403() {
        RegisterRequest req = new RegisterRequest("username", "password", "email");
        RegisterResult firstResult = serverFacade.register(req);
        assertEquals("username", firstResult.username());
        assertNull(firstResult.message(), "normal registration returned an error message");

        RegisterResult secondResult = serverFacade.register(req);
        assertNull(secondResult.username(), "duplicate username registration returned a username");
        assertNull(secondResult.authToken(), "duplicate username registration returned an authToken");
        assertNotNull(secondResult.message(), "duplicate username registration did not return a message");
    }
}
