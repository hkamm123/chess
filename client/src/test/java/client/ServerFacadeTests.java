package client;

import org.junit.jupiter.api.*;
import server.*;

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
    static void cleanup() {
        serverFacade.clear();
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

    @Test
    public void successClear() {
        LogoutResult result = serverFacade.clear();
        assertNull(result.message(), "clear returned an error message");
    }

    @Test
    public void successLogin() {
        RegisterRequest regReq = new RegisterRequest("username", "password", "email");
        RegisterResult regResult = serverFacade.register(regReq);
        String existingAuthToken = regResult.authToken();

        RegisterResult loginResult = serverFacade.login(new LoginRequest("username", "password"));
        assertNotEquals(existingAuthToken, loginResult.authToken(), "login did not provide a unique auth");
        assertNull(loginResult.message(), "login result contained an error message");
        assertEquals("username", loginResult.username(), "login did not result in expected username");
    }

    @Test
    public void loginFailsWhenBadCredentials() {
        LoginRequest req = new LoginRequest("badUsername", "badPassword");
        RegisterResult result = serverFacade.login(req);
        assertNull(result.username(), "bad credentials login gave a username when not expected");
        assertNull(result.authToken(), "bad credentials login gave an authtoken when not expected");
        assertNotNull(result.message(), "bad credentials login did not result in error message");
    }

    @Test
    public void successLogout() {
        RegisterRequest regReq = new RegisterRequest("username", "password", "email");
        RegisterResult regRes = serverFacade.register(regReq);
        assertNull(regRes.message());
        assertEquals("username", regRes.username());
        String auth = regRes.authToken();
        LogoutResult logoutResult = serverFacade.logout(auth);
        assertNull(logoutResult.message(), "logout returned an error message when not expected");
    }

    @Test
    public void logoutFailWhenNotLoggedIn() {
        String auth = "some nonexistent auth";
        LogoutResult logoutResult = serverFacade.logout(auth);
        assertNotNull(logoutResult.message(), "bad logout did not return an error message");
    }

    @Test
    public void successListGames() {
        RegisterRequest regReq = new RegisterRequest("username", "password", "email");
        RegisterResult regRes = serverFacade.register(regReq);
        assertNull(regRes.message());
        String auth = regRes.authToken();

        ListResult result = serverFacade.listGames(auth);
        assertNull(result.message(), "result had an error message when not expected");
        assertEquals(0, result.games().size(), "resulting games list did not exist or had wrong size");
    }

    @Test
    public void listGamesFailWhenBadAuth() {
        ListResult result = serverFacade.listGames("bad auth");
        assertNotNull(result.message(), "result did not contain an error message");
        assertNull(result.games(), "result contained a games list when not expected");
    }

//    TODO: write tests for listGames
//    TODO: write tests for createGame
//    TODO: write tests for joinGame
}
