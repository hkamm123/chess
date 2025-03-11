package dataaccess;

import model.UserData;
import org.junit.jupiter.api.*;
import org.mindrot.jbcrypt.BCrypt;
import service.UserService;

import java.sql.SQLException;

import static dataaccess.DatabaseManager.getConnection;
import static org.junit.jupiter.api.Assertions.*;

public class MySQLUserDaoTest {
    private static UserDao sqlUserDao;

    private String hashedpw = BCrypt.hashpw("testPassword", BCrypt.gensalt());

    @BeforeAll
    public static void configure() throws DataAccessException {
        try {
            sqlUserDao = new MySQLUserDao();
        } catch (DataAccessException ex) {
            throw new AssertionError(ex.getMessage());
        }
    }

    @BeforeEach
    public void setup() {
        try (var conn = getConnection()) {
            var statement = """
                    INSERT INTO users (username, passwordHash, email) 
                    VALUES ('testUser', ?, 'testEmail')
                    """;
            try (var preparedStatement = conn.prepareStatement(statement)) {
                preparedStatement.setString(1, hashedpw);
                preparedStatement.executeUpdate();
            }
        } catch (Exception ex) {
            throw new AssertionError(
                    "EXCEPTION DURING SETUP: " + ex.getMessage()
            );
        }
    }

    @Test
    public void successGetUser() {
        UserData expected = new UserData("testUser", hashedpw, "testEmail");
        UserData actual = null;
        try {
            actual = sqlUserDao.getUser("testUser");
        } catch (DataAccessException ex) {
            throw new AssertionError(ex.getMessage());
        }
        assertEquals(expected, actual);
    }

    @Test
    public void getUserReturnsNullWhenUserNotExists() {
        UserData received;
        try {
            received = sqlUserDao.getUser("fakeUser");
        } catch (DataAccessException ex) {
            throw new AssertionError(ex.getMessage());
        }
        assertNull(received);
    }

    @Test
    public void successCreateUser() {
        UserData userToCreate = new UserData(
                "newUser", "newHash", "testEmail"
        );
        // same email as the user that was made in setup,
        // so this user will be deleted in cleanup

        // try to create the new user
        try {
            sqlUserDao.createUser(userToCreate);
        } catch (DataAccessException ex) {
            throw new AssertionError(ex.getMessage());
        }

        // assert that the user is in the database
        String resultUsername = "";
        try (var conn = getConnection()) {
            var statement = """
                    SELECT username FROM users WHERE username = 'newUser'
                    """;
            try (var preparedStatement = conn.prepareStatement(statement)) {
                var resultSet = preparedStatement.executeQuery();
                while (resultSet.next()) {
                    resultUsername = resultSet.getString("username");
                }
            }
        } catch (DataAccessException | SQLException ex) {
            throw new AssertionError(ex.getMessage());
        }
        assertEquals("newUser", resultUsername);
    }

    @Test
    public void createUserFailsWhenNullUsername() {
        UserData userToCreate = new UserData(
                null,
                "somePassword",
                "someEmail"
        );

        assertThrows(
                DataAccessException.class,
                () -> sqlUserDao.createUser(userToCreate)
        );
    }

    @Test
    public void successIsValidCredentials() {
        boolean actual = false;
        try {
            actual = sqlUserDao.isValidCredentials(
                    "testUser",
                    "testPassword"
            );
        } catch (DataAccessException ex) {
            throw new AssertionError(ex.getMessage());
        }
        assertTrue(actual);

        boolean actual2 = true;
        try {
            actual2 = sqlUserDao.isValidCredentials(
                    "testUser",
                    "wrongPassword"
            );
        } catch (DataAccessException ex) {
            throw new AssertionError(ex.getMessage());
        }
        assertFalse(actual2);
    }

    @Test
    public void isValidCredentialsFalseWhenNullUsername() {
        try {
            boolean isValid = sqlUserDao.isValidCredentials(null, "testPassword");
            assertFalse(isValid);
        } catch (DataAccessException ex) {
            throw new AssertionError(ex.getMessage());
        }
    }

    @Test
    public void successClear() {
        java.sql.Connection conn;

        // get the connection
        try {
            conn = getConnection();
        } catch (DataAccessException ex) {
            throw new AssertionError(ex.getMessage());
        }

        // begin a transaction
        try {
            var beginTransactionStatement = "START TRANSACTION;";
            try (var preparedStatement = conn.prepareStatement(beginTransactionStatement)) {
                preparedStatement.execute();
            }
        } catch (Exception ex) {
            throw new AssertionError(ex.getMessage());
        }

        try {
            // clear db, assert no users
            sqlUserDao.clear();

            var statement = """
                    SELECT * from users
                    """;
            try (var preparedStatement = conn.prepareStatement(statement)) {
                var resultSet = preparedStatement.executeQuery();
                assertFalse(resultSet.next());
            }
        } catch (Exception ex) {
            throw new AssertionError(ex.getMessage());
        }

        // rollback transaction
        try {
            var beginTransactionStatement = "ROLLBACK;";
            try (var preparedStatement = conn.prepareStatement(beginTransactionStatement)) {
                preparedStatement.execute();
            }
        } catch (Exception ex) {
            throw new AssertionError(ex.getMessage());
        }

        //close the connection
        try {
            conn.close();
        } catch (SQLException ex) {
            throw new AssertionError(ex.getMessage());
        }
    }

    @AfterEach
    public void cleanup() {
        SQLDaoTestUtils.cleanup();
    }
}
