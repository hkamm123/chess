package service;

import dataaccess.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.RegisterRequest;
import server.RegisterResult;

import static org.junit.jupiter.api.Assertions.*;

public class UserServiceTest {
    private UserDao testUserDao;
    private AuthDao testAuthDao;
    private UserService testUserService;

    @BeforeEach
    public void setup() {
        testUserDao = new MemoryUserDao();
        testAuthDao = new MemoryAuthDao();
        testUserService = new UserService(testUserDao, testAuthDao);
    }

    @Test
    public void SuccessRegister() {
        RegisterRequest testRegReq = new RegisterRequest("username", "password", "email");

        try {
            RegisterResult actualResult = testUserService.register(testRegReq);
            assertEquals("username", actualResult.username(),
                    "registration did not return the given username");
            assertNull(actualResult.message(), "result message is not null");
            assertNotNull(actualResult.authToken(), "result did not return an authToken");
            assertTrue(testAuthDao.containsToken(actualResult.authToken()),
                    "auth DAO did not receive the auth token from the registration");
        } catch (Exception ex) {
            throw new AssertionError("exception thrown when not expected: " + ex.getMessage());
        }
    }

    @Test
    public void RegisterFailWhenNoUsernameGiven() {
        RegisterRequest testRegReq = new RegisterRequest(null, "password", "email");

        try {
            RegisterResult actualResult = testUserService.register(testRegReq);
            assertEquals("Error: bad request", actualResult.message(),
                    "message in result was not the bad request message");
            assertNull(actualResult.username(), "result username is not null");
            assertNull(actualResult.authToken(), "result auth token is not null");
        } catch (Exception ex) {
            throw new AssertionError("exception thrown when not expected: " + ex.getMessage());
        }
    }
}
