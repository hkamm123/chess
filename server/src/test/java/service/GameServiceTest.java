package service;

import dataaccess.AuthDao;
import dataaccess.GameDao;
import dataaccess.MemoryAuthDao;
import dataaccess.MemoryGameDao;
import model.AuthData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;

public class GameServiceTest {
    GameDao testGameDao;
    AuthDao testAuthDao;
    GameService testGameService;

    @BeforeEach
    public void setup() {
        testGameDao = new MemoryGameDao();
        testAuthDao = new MemoryAuthDao();
        testGameService = new GameService(testGameDao, testAuthDao);
    }

    @Test
    public void successClear() {
        int resultGameID = testGameDao.createGame("newGame");
        AuthData auth = testAuthDao.createAuth("username");
        testGameService.clear();
        assertFalse(testAuthDao.containsToken(auth.authToken()), "auth dao had token, should be empty");
        assertFalse(testGameDao.containsID(resultGameID), "game dao had and ID, should be empty");
    }
}
