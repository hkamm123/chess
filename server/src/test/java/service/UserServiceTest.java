package service;

import dataaccess.*;
import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mindrot.jbcrypt.BCrypt;
import server.*;

import java.sql.SQLException;

import static dataaccess.DatabaseManager.getConnection;
import static org.junit.jupiter.api.Assertions.*;

public class UserServiceTest {
    private UserDao testUserDao;
    private AuthDao testAuthDao;
    private UserService testUserService;

    @BeforeEach
    public void setup() {
        try {
            testUserDao = new MySQLUserDao();
            testAuthDao = new MySQLAuthDao();
        } catch (DataAccessException ex) {
            throw new AssertionError(ex.getMessage());
        }
        testUserService = new UserService(testUserDao, testAuthDao);
    }

    @Test
    public void successRegister() {
        RegisterRequest testRegReq = new RegisterRequest("testUser", "password", "email");

        try {
            RegisterResult actualResult = testUserService.register(testRegReq);
            assertEquals(
                    "testUser",
                    actualResult.username(),
                    "registration did not return the given username"
            );
            assertNull(actualResult.message(), "result message is not null");
            assertNotNull(actualResult.authToken(), "result did not return an authToken");
            assertTrue(
                    testAuthDao.containsToken(actualResult.authToken()),
                    "auth DAO did not receive the auth token from the registration"
            );
        } catch (Exception ex) {
            throw new AssertionError("exception thrown when not expected: " + ex.getMessage());
        }
    }

    @Test
    public void registerFailWhenNoUsernameGiven() {
        RegisterRequest testRegReq = new RegisterRequest(null, "password", "email");

        try {
            RegisterResult actualResult = testUserService.register(testRegReq);
            assertEquals(
                    "Error: bad request", actualResult.message(),
                    "message in result was not the bad request message"
            );
            assertNull(actualResult.username(), "result username is not null");
            assertNull(actualResult.authToken(), "result auth token is not null");
        } catch (Exception ex) {
            throw new AssertionError("exception thrown when not expected: " + ex.getMessage());
        }
    }

    @Test
    public void successLogin() {
        try {
            String hashedPassword = BCrypt.hashpw("password", BCrypt.gensalt());
            testUserDao.createUser(new UserData("testUser", hashedPassword, "email"));
            RegisterResult actualResult = testUserService.login(
                    new LoginRequest("testUser", "password")
            );
            assertEquals(
                    "testUser", actualResult.username(),
                    "registration did not return the given username"
            );
            assertNull(
                    actualResult.message(),
                    "result message is not null"
            );
            assertNotNull(actualResult.authToken(), "result did not return an authToken");
            assertTrue(
                    testAuthDao.containsToken(actualResult.authToken()),
                    "auth DAO did not receive the auth token from the registration"
            );
        } catch (Exception ex) {
            throw new AssertionError("exception thrown when not expected: " + ex.getMessage());
        }
    }

    @Test
    public void loginFailWhenUserNotRegistered() {
        RegisterResult actualResult = testUserService.login(
                new LoginRequest("unregisteredUsername", "unregisteredPassword"));
        assertNull(actualResult.username(), "result contained a non-null username");
        assertNull(actualResult.authToken(), "result contained a non-null auth token");
        assertNotNull(actualResult.message(), "result did not contain an error message");
    }

    @Test
    public void successLogout() {
        try {
            testUserDao.createUser(new UserData("testUser", "password", "email"));
            AuthData auth = testAuthDao.createAuth("testUser");
            LogoutResult actualResult = testUserService.logout(auth.authToken());
            assertNull(actualResult.message(), "result message was not null");
            assertFalse(testAuthDao.containsToken(auth.authToken()));
        } catch (Exception ex) {
            throw new AssertionError("exception thrown when not expected");
        }
    }

    @Test
    public void logoutFailWhenGivenBadAuthToken() {
        try {
            testUserDao.createUser(new UserData("testUser", "password", "email"));
            LogoutResult actualResult = testUserService.logout("badAuthToken");
            assertNotNull(actualResult.message());
        } catch (Exception ex) {
            throw new AssertionError("exception thrown when not expected");
        }
    }

    @Test
    public void successClear() {
        try {
            testUserDao.createUser(new UserData("testUser", "password", "email"));
            AuthData auth = testAuthDao.createAuth("testUser");
            testUserService.clear();
            assertFalse(testAuthDao.containsToken(auth.authToken()),
                    "authDao contains a token, should be empty");
            assertFalse(testUserDao.isValidCredentials("testUser", "password"),
                    "userDao contains credentials, should be empty");
        } catch (Exception ex) {
            throw new AssertionError("exception thrown when not expected: " + ex.getMessage());
        }
    }

    @AfterEach
    public void cleanup() {
        try (var conn = getConnection()) {
            int userID = 0;
            try (var preparedStatement = conn.prepareStatement(
                    "SELECT userID FROM users WHERE username = 'testUser'")) {
                var resultSet = preparedStatement.executeQuery();
                if (resultSet.next()) {
                    userID = resultSet.getInt("userID");
                }
            }
            try (var preparedStatement = conn.prepareStatement(
                    "DELETE FROM sessions WHERE userID = " + userID)) {
                preparedStatement.execute();
            }
            try (var preparedStatement = conn.prepareStatement(
                    "DELETE FROM users WHERE username = 'testUser'")) {
                preparedStatement.execute();
            }
        } catch (DataAccessException | SQLException ex) {
            throw new AssertionError(ex.getMessage());
        }
    }
}
