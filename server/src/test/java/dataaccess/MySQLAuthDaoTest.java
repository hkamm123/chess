package dataaccess;

import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.*;

import java.sql.SQLException;

import static dataaccess.DatabaseManager.getConnection;
import static org.junit.jupiter.api.Assertions.*;

public class MySQLAuthDaoTest {
    private static MySQLAuthDao sqlAuthDao;
    private static MySQLUserDao sqlUserDao;

    @BeforeAll
    public static void configure() throws DataAccessException {
        try {
            sqlUserDao = new MySQLUserDao();
            sqlAuthDao = new MySQLAuthDao();
        } catch (DataAccessException ex) {
            throw new AssertionError(ex.getMessage());
        }
    }

    @BeforeEach
    public void setup() {
        try {
            sqlUserDao.createUser(new UserData(
                    "testUser",
                    "testPassword",
                    "testEmail"
            ));
        } catch (DataAccessException ex) {
            throw new AssertionError(ex.getMessage());
        }
    }

    private int getUserID(String username) throws DataAccessException {
        int userID;
        var getUserIDStatement = """
                SELECT (userID) FROM users WHERE username = ?
                """;
        try (var conn = getConnection()) {
            try (var preparedStatement = conn.prepareStatement(getUserIDStatement)) {
                preparedStatement.setString(1, "testUser");
                var resultSet = preparedStatement.executeQuery();
                resultSet.next();
                userID = resultSet.getInt("userID");
            }
        } catch (SQLException | DataAccessException ex) {
            throw new DataAccessException(ex.getMessage());
        }
        return userID;
    }

    @Test
    public void successCreateAuth() {
        AuthData actual = null;

        try (var conn = getConnection()) {
            actual = sqlAuthDao.createAuth("testUser");
            AuthData expected = new AuthData(
                    "someAuthToken",
                    "testUser"
            );

            assertEquals(expected.username(), actual.username());

            int userID = getUserID("testUser");

            String deleteStatement = """
                        DELETE FROM sessions WHERE userID = ?
                    """;
            try (var preparedStatement = conn.prepareStatement(deleteStatement)) {
                preparedStatement.setInt(1, userID);
                preparedStatement.executeUpdate();
            }
        } catch (DataAccessException | SQLException ex) {
            throw new AssertionError(ex.getMessage());
        }
    }

    @Test
    public void createAuthFailsWhenUserNotExists() {
        assertThrows(DataAccessException.class, () -> sqlAuthDao.createAuth("fakeUser"));
    }

    private void manuallyAddAuthData(int userID, String authToken) throws DataAccessException {
        try (var conn = getConnection()) {
            var insertStatement = """
                    INSERT INTO `chess`.`sessions` (userID, authToken) VALUES (?, ?)
                    """;
            try (var preparedStatement = conn.prepareStatement(insertStatement)) {
                preparedStatement.setInt(1, userID);
                preparedStatement.setString(2, authToken);
                preparedStatement.executeUpdate();
            }
        } catch (DataAccessException | SQLException ex) {
            throw new DataAccessException(ex.getMessage());
        }
    }

    private void manuallyCleanupAuthData(String authToken) throws DataAccessException {
        try (var conn = getConnection()) {
            var cleanupStatement = """
                    DELETE FROM sessions WHERE authToken = ?
                    """;
            try (var preparedStatement = conn.prepareStatement(cleanupStatement)) {
                preparedStatement.setString(1, authToken);
                preparedStatement.executeUpdate();
            }
        } catch (SQLException | DataAccessException ex) {
            throw new DataAccessException(ex.getMessage());
        }
    }

    @Test
    public void successGetUsername() {
        String expectedUsername = "testUser";
        String manualToken = "someAuthToken";
        // manually insert an auth token
        try (var conn = getConnection()) {
            int userID = getUserID(expectedUsername);
            manuallyAddAuthData(userID, manualToken);

            String actual = sqlAuthDao.getUsername(manualToken);
            assertEquals(expectedUsername, actual);

            manuallyCleanupAuthData(manualToken);
        } catch (DataAccessException | SQLException ex) {
            throw new AssertionError(ex.getMessage());
        }
    }

    @Test
    public void getUsernameFailsWhenUserNotExists() {
        assertThrows(DataAccessException.class, () -> sqlAuthDao.getUsername("fakeAuthToken"));
    }

    @Test
    public void successDeleteAuth() {
        boolean authDeleted = false;
        try {
            String authToken = "someAuthToken";
            int userID = getUserID("testUser");
            manuallyAddAuthData(userID, authToken);
            authDeleted = sqlAuthDao.deleteAuth(authToken);
            manuallyCleanupAuthData(authToken);
        } catch (DataAccessException ex) {
            throw new AssertionError(ex.getMessage());
        }

        assertTrue(authDeleted);
    }

    @Test
    public void deleteAuthFalseWhenAuthTokenNotExists() {
        boolean authDeleted = true;
        try {
            authDeleted = sqlAuthDao.deleteAuth("fakeAuthToken");
        } catch (DataAccessException ex) {
            throw new AssertionError(ex.getMessage());
        }

        assertFalse(authDeleted);
    }

    @Test
    public void successContainsToken() {
        String testToken = "testAuthToken";
        try {
            int userID = getUserID("testUser");
            assertFalse(sqlAuthDao.containsToken((testToken)));
            manuallyAddAuthData(userID, testToken);
            assertTrue(sqlAuthDao.containsToken(testToken));
            manuallyCleanupAuthData(testToken);
        } catch (DataAccessException ex) {
            throw new AssertionError(ex.getMessage());
        }
    }

    @Test
    public void containsTokenFalseWhenNullAuthToken() {
        String testToken = null;
        try {
            assertFalse(sqlAuthDao.containsToken(testToken));
        } catch (DataAccessException ex) {
            throw new AssertionError(ex.getMessage());
        }
    }

    @Test
    public void successClear() {
        // manually add data
        String testToken = "testAuthToken";
        try {
            int userID = getUserID("testUser");
            manuallyAddAuthData(userID, testToken);
        } catch (DataAccessException ex) {
            throw new AssertionError(ex.getMessage());
        }

        try (var conn = getConnection()) { // get the connection
            // start transaction
            try (var preparedStatement = conn.prepareStatement("START TRANSACTION;")) {
                preparedStatement.execute();
            }

            sqlAuthDao.clear(); // clear db

            // assert no sessions
            String queryStatement = """
                    SELECT * FROM sessions
                    """;
            try (var preparedStatement = conn.prepareStatement(queryStatement)) {
                var resultSet = preparedStatement.executeQuery();
                assertFalse(resultSet.next());
            }

            // rollback
            try (var preparedStatement = conn.prepareStatement("ROLLBACK")) {
                preparedStatement.execute();
            }

            // cleanup manually added data
            manuallyCleanupAuthData(testToken);
        } catch (DataAccessException | SQLException ex) {
            throw new AssertionError(ex.getMessage());
        }
    }

    @AfterEach
    public void cleanup() {
        SQLDaoTestUtils.cleanup();
    }
}
