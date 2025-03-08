package dataaccess;

import model.UserData;
import org.junit.jupiter.api.*;
import service.UserService;

import java.sql.SQLException;

public class MySQLUserDaoTest {
    private static UserDao memUserDao;
    private static UserDao sqlUserDao;

    @BeforeAll
    public static void configure() throws DataAccessException {
        memUserDao = new MemoryUserDao();
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
            VALUES ('testUser', 'testHash', 'testEmail')
            """;
            try (var preparedStatement = conn.prepareStatement(statement)) {
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
        UserData expected = new UserData("testUser", "testHash", "testEmail");
        UserData actual = null;
        try{
            actual = sqlUserDao.getUser("testUser");
        } catch (DataAccessException ex) {
            throw new AssertionError(ex.getMessage());
        }
        Assertions.assertEquals(expected, actual);
    }

    @AfterEach
    public void cleanup() {
        try {
            var conn = DatabaseManager.getConnection();
            var statement = """
            DELETE FROM `chess`.`users` WHERE username = 'testUser'
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
