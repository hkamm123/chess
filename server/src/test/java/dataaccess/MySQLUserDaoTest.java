package dataaccess;

import model.UserData;
import org.junit.jupiter.api.*;
import org.mindrot.jbcrypt.BCrypt;
import service.UserService;

import java.sql.SQLException;

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
        try {
            var conn = DatabaseManager.getConnection();
            var statement = """
            INSERT INTO `chess`.`users` (username, passwordHash, email) 
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
        try{
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
    public void successCreateUser() { // TODO: write this test
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
        try {
            var conn = DatabaseManager.getConnection();
            var statement = """
                    SELECT username FROM `chess`.`users` WHERE username = 'newUser'
                    """;
            try(var preparedStatement = conn.prepareStatement(statement)) {
                var resultSet = preparedStatement.executeQuery();
                while(resultSet.next()) {
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
    public void isValidCredentialsThrowsExceptionWhenNullUsername() {
        assertThrows(
                DataAccessException.class,
                () -> sqlUserDao.isValidCredentials(null, "testPassword")
                );
    }

    @AfterEach
    public void cleanup() {
        try {
            var conn = DatabaseManager.getConnection();
            var statement = """
            DELETE FROM `chess`.`users` WHERE email = 'testEmail'
            """;
            try (var preparedStatement = conn.prepareStatement(statement)) {
                preparedStatement.executeUpdate();
            }
        } catch (Exception ex) {
            throw new AssertionError(
                    "EXCEPTION DURING CLEANUP: " + ex.getMessage()
            );
        }
    }
}
