package dataaccess;

import model.UserData;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import service.UserService;

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

    @Test
    public void successGetUser() {
        UserData expected = new UserData("testUsername", "testPassword", "testEmail");
        UserData actual = sqlUserDao.getUser("testUsername");
        Assertions.assertEquals(expected, actual);
    }

    @AfterEach
    public void cleanup() {

    }
}
