package service;

import dataaccess.*;
import model.AuthData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.*;

import static chess.ChessGame.TeamColor.WHITE;
import static org.junit.jupiter.api.Assertions.*;

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
    public void successListGames() throws DataAccessException {
        testGameDao.createGame("sample game");
        testGameDao.createGame("second sample game");
        AuthData auth;
        try {
            auth = testAuthDao.createAuth("username");
        } catch (DataAccessException ex) {
            throw new AssertionError(ex.getMessage());
        }
        assertTrue(testAuthDao.containsToken(auth.authToken()), "auth token not created in auth dao");

        ListResult testResult = testGameService.listGames(auth.authToken());
        assertEquals(2, testResult.games().size(),
                "expected 2 games, received: " + testResult.games().size());
        assertNull(testResult.message(), "message received was not null");
    }

    @Test
    public void listGamesFailWhenBadAuthGiven() {
        testGameDao.createGame("a sample game");
        ListResult testResult = testGameService.listGames("a bad auth token");
        assertNull(testResult.games(), "result games is not null");
        assertNotNull(testResult.message(), "result message is null");
    }

    @Test
    public void successCreateGame() throws DataAccessException {
        AuthData auth;
        try {
            auth = testAuthDao.createAuth("username");
        } catch (DataAccessException ex) {
            throw new AssertionError(ex.getMessage());
        }
        CreateResult testResult = testGameService.createGame(
                new CreateRequest("a new game"),
                auth.authToken()
        );
        assertNull(testResult.message(), "result message not null");
        assertNotNull(testResult.gameID(), "result did not include game ID");
        int resultID = testResult.gameID();
        assertTrue(testGameDao.containsID(resultID), "game not created in db");
    }

    @Test
    public void createGameFailWhenBadAuthGiven() {
        CreateResult testResult = testGameService.createGame(
                new CreateRequest("gameName"),
                "bad auth token"
        );
        assertNull(testResult.gameID(), "result returned a gameID when a bad auth was given");
        assertNotNull(testResult.message(), "result did not return a message");
    }

    @Test
    public void successJoinGame() {
        int gameID = testGameDao.createGame("new game");
        AuthData auth;
        try {
            auth = testAuthDao.createAuth("username");
        } catch (DataAccessException ex) {
            throw new AssertionError(ex.getMessage());
        }
        JoinResult testResult = testGameService.joinGame(
                new JoinRequest(WHITE, gameID),
                auth.authToken()
        );
        assertNull(testResult.message(), "result returned a message");
    }

    @Test
    public void joinGameFailWhenBadAuthGiven() {
        int gameID = testGameDao.createGame("new game");
        JoinResult testResult = testGameService.joinGame(
                new JoinRequest(WHITE, gameID),
                "bad auth token"
        );
        assertNotNull(testResult.message(), "test result did not return a message");
    }

    @Test
    public void successClear() {
        boolean containsToken;
        int resultGameID = testGameDao.createGame("newGame");
        AuthData auth;
        try {
            auth = testAuthDao.createAuth("username");
            testGameService.clear();
            containsToken = testAuthDao.containsToken(auth.authToken());
        } catch (DataAccessException ex) {
            throw new AssertionError(ex.getMessage());
        }
        assertFalse(containsToken, "auth dao had token, should be empty");
        assertFalse(testGameDao.containsID(resultGameID), "game dao had and ID, should be empty");
    }
}
