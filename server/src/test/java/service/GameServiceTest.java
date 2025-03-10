package service;

import dataaccess.*;
import model.AuthData;
import model.UserData;
import org.eclipse.jetty.server.Authentication;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.*;

import java.sql.SQLException;

import static chess.ChessGame.TeamColor.WHITE;
import static dataaccess.DatabaseManager.getConnection;
import static org.junit.jupiter.api.Assertions.*;

public class GameServiceTest {
    GameDao testGameDao;
    AuthDao testAuthDao;
    UserDao testUserDao;
    GameService testGameService;

    @BeforeEach
    public void setup() {
        try {
            testGameDao = new MySQLGameDao();
            testAuthDao = new MySQLAuthDao();
            testUserDao = new MySQLUserDao();
        } catch (DataAccessException ex) {
            throw new AssertionError(ex.getMessage());
        }
        testGameService = new GameService(testGameDao, testAuthDao);
    }

    @Test
    public void successListGames() throws DataAccessException {
        testGameDao.createGame("testGame");
        AuthData auth;
        try {
            testUserDao.createUser(new UserData("testUser", "testPassword", "testEmail"));
            auth = testAuthDao.createAuth("testUser");
        } catch (DataAccessException ex) {
            throw new AssertionError(ex.getMessage());
        }
        assertTrue(testAuthDao.containsToken(auth.authToken()), "auth token not created in auth dao");

        ListResult testResult = testGameService.listGames(auth.authToken());
        assertEquals(1, testResult.games().size(),
                "expected 2 games, received: " + testResult.games().size());
        assertNull(testResult.message(), "message received was not null");
    }

    @Test
    public void listGamesFailWhenBadAuthGiven() {
        try {
            testGameDao.createGame("testGame");
        } catch (DataAccessException ex) {
            throw new AssertionError(ex.getMessage());
        }
        ListResult testResult = testGameService.listGames("a bad auth token");
        assertNull(testResult.games(), "result games is not null");
        assertNotNull(testResult.message(), "result message is null");
    }

    @Test
    public void successCreateGame() throws DataAccessException {
        AuthData auth;
        try {
            testUserDao.createUser(new UserData("testUser", "testPassword", "testEmail"));
            auth = testAuthDao.createAuth("testUser");
        } catch (DataAccessException ex) {
            throw new AssertionError(ex.getMessage());
        }
        CreateResult testResult = testGameService.createGame(
                new CreateRequest("testGame"),
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
                new CreateRequest("testGame"),
                "bad auth token"
        );
        assertNull(testResult.gameID(), "result returned a gameID when a bad auth was given");
        assertNotNull(testResult.message(), "result did not return a message");
    }

    @Test
    public void successJoinGame() {
        AuthData auth;
        int gameID;
        try {
            testUserDao.createUser(new UserData("testUser", "testPassword", "testEmail"));
            gameID = testGameDao.createGame("testGame");
            auth = testAuthDao.createAuth("testUser");
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
        int gameID;
        try {
            gameID = testGameDao.createGame("testGame");
        } catch (DataAccessException ex) {
            throw new AssertionError(ex.getMessage());
        }
        JoinResult testResult = testGameService.joinGame(
                new JoinRequest(WHITE, gameID),
                "bad auth token"
        );
        assertNotNull(testResult.message(), "test result did not return a message");
    }

    @Test
    public void successClear() {
        try {
            int resultGameID = testGameDao.createGame("testGame");
            testUserDao.createUser(new UserData("testUser", "testPassword", "testEmail"));
            AuthData auth = testAuthDao.createAuth("testUser");
            testGameService.clear();
            boolean containsToken = testAuthDao.containsToken(auth.authToken());
            assertFalse(containsToken, "auth dao had token, should be empty");
            assertFalse(testGameDao.containsID(resultGameID), "game dao had and ID, should be empty");
        } catch (DataAccessException ex) {
            throw new AssertionError(ex.getMessage());
        }
    }

    @AfterEach
    public void cleanup() {
        try (var conn = getConnection()) {
            try (var preparedStatement = conn.prepareStatement(
                    "DELETE FROM games WHERE gameName = 'testGame'")) {
                preparedStatement.execute();
            }
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
