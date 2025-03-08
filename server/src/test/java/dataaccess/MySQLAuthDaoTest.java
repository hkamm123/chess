package dataaccess;

import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.SQLException;

import static dataaccess.DatabaseManager.getConnection;
import static org.junit.jupiter.api.Assertions.assertThrows;

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

    @Test
    public void successCreateAuth() {
        AuthData actual = null;

        try (var conn = getConnection()){
            actual = sqlAuthDao.createAuth("testUser");
            AuthData expected = new AuthData(
                    "someAuthToken",
                    "testUser"
            );

            Assertions.assertEquals(expected.username(), actual.username());

            int userID;
            var getUserIDStatement = """
                SELECT (userID) FROM users WHERE username = ?
                """;
            try (var preparedStatement = conn.prepareStatement(getUserIDStatement)) {
                preparedStatement.setString(1, "testUser");
                var resultSet = preparedStatement.executeQuery();
                resultSet.next();
                userID = resultSet.getInt("userID");
            }

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

    @AfterEach
    public void cleanup() {
        try {
            var conn = getConnection();
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
