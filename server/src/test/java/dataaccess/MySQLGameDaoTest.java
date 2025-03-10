package dataaccess;

import chess.ChessGame;
import chess.ChessPosition;
import com.google.gson.Gson;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.*;
import spark.utils.Assert;

import java.sql.SQLException;
import java.sql.SQLWarning;
import java.util.Collection;

import static chess.ChessGame.TeamColor.BLACK;
import static dataaccess.DatabaseManager.getConnection;
import static org.junit.jupiter.api.Assertions.*;

public class MySQLGameDaoTest {
    private static GameDao sqlGameDao;
    private static UserDao sqlUserDao;
    private static AuthDao sqlAuthDao;
    private Gson serializer = new Gson();
    private String authToken;

    @BeforeAll
    public static void configure() throws DataAccessException {
        try {
            sqlUserDao = new MySQLUserDao();
            sqlAuthDao = new MySQLAuthDao();
            sqlGameDao = new MySQLGameDao();
        } catch (DataAccessException ex) {
            throw new AssertionError(ex.getMessage());
        }
    }

    @BeforeEach
    public void setup() {
        // create a user, authdata, and a test game
        try (var conn = getConnection()) {
            sqlUserDao.createUser(new UserData("testUser", "testPassword", "testEmail"));
            AuthData authData = sqlAuthDao.createAuth("testUser");
            authToken = authData.authToken();
            String json = serializer.toJson(new ChessGame());
            String insertStatement = """
                    INSERT INTO games (gameName, chessGameJson) VALUES (?, ?)
                    """;
            try (var preparedStatement = conn.prepareStatement(insertStatement)) {
                preparedStatement.setString(1, "testGame");
                preparedStatement.setString(2, json);
                preparedStatement.executeUpdate();
            }
        } catch (DataAccessException | SQLException ex) {
            throw new AssertionError(ex.getMessage());
        }
    }

    @Test
    public void successGetGames() {
        try {
            Collection<GameData> receivedGames = sqlGameDao.getGames();
            assertEquals(1, receivedGames.size(), "size of received list incorrect");
            GameData gameData = receivedGames.iterator().next();
            assertEquals("testGame", gameData.gameName(), "returned game name incorrect");
        } catch (DataAccessException ex) {
            throw new AssertionError(ex.getMessage());
        }
    }

    @Test public void getGamesFailsWhenDatabaseDown() {
        // simulate database being down by setting the wrong password
        DatabaseManager.setPassword("invalidPassword");
        Assertions.assertThrows(DataAccessException.class, () -> sqlGameDao.getGames());
        try {
            DatabaseManager.revertPassword();
        } catch (DataAccessException ex) {
            throw new AssertionError("failed to reset db password because of error: " + ex.getMessage());
        }
    }

    @Test
    public void successCreateGame() {
        try (var conn = getConnection()){
            int receivedID = sqlGameDao.createGame("newTestGame");

            // manually get the ID of the game with name "newTestGame"
            int actualID = getGameID("newTestGame");
            assertEquals(actualID, receivedID, "game ids were different");
        } catch (DataAccessException | SQLException ex) {
            throw new AssertionError(ex.getMessage());
        }
    }

    @Test
    public void createGameFailsWhenNullGameName() {
        assertThrows(DataAccessException.class, () -> sqlGameDao.createGame(null));
    }

    private int getGameID(String gameName) {
        try (var conn = getConnection()) {
            try (var preparedStatement = conn.prepareStatement(
                    "SELECT (gameID) FROM games WHERE gameName = ?")) {
                preparedStatement.setString(1, gameName);
                var resultSet = preparedStatement.executeQuery();
                /* this statement will inherently check for game creation
                 by throwing an exception if the resultSet is empty
                 */
                resultSet.next();
                return resultSet.getInt("gameID");
            }
        } catch (DataAccessException | SQLException ex) {
            throw new AssertionError(ex.getMessage());
        }
    }

    @Test
    public void successContainsID() {
        boolean containsID = false;
        try {
            containsID = sqlGameDao.containsID(getGameID("testGame"));
            assertTrue(containsID);
            containsID = sqlGameDao.containsID(getGameID("testGame") + 10); // this ID shouldn't be there
            assertFalse(containsID);
        } catch (DataAccessException ex) {
            throw new AssertionError(ex.getMessage());
        }
    }

    @Test
    public void containsIDThrowsExceptionWhenNullGameID() {
        assertThrows(DataAccessException.class, () -> sqlGameDao.containsID(null));
    }

    private int getUserID(String username) throws DataAccessException {
        try (var conn = getConnection()) {
            String queryStatement = "SELECT userID FROM users WHERE username = '" + username + "'";
            try (var preparedStatement = conn.prepareStatement(queryStatement)) {
                var resultSet = preparedStatement.executeQuery();
                resultSet.next();
                return resultSet.getInt("userID");
            }
        } catch (DataAccessException | SQLException ex) {
            throw new DataAccessException(ex.getMessage());
        }
    }

    @Test
    public void successSetPlayerColor() {
        try {
            int gameID = getGameID("testGame");
            sqlGameDao.setPlayerColor(gameID, BLACK, "testUser");

            // find the blackUserID and assert that it corresponds to the username "testUser"
            String getBlackUserIDStatement = "SELECT blackUserID FROM games WHERE gameID = " + gameID;
            try (var preparedStatement = getConnection().prepareStatement(getBlackUserIDStatement)) {
                var resultSet = preparedStatement.executeQuery();
                resultSet.next();
                int receivedBlackUserID = resultSet.getInt("blackUserID");
                int expectedBlackUserID = getUserID("testUser");
                assertEquals(expectedBlackUserID, receivedBlackUserID, "blackUserID is not correct.");
            }
        } catch (DataAccessException | SQLException ex) {
            throw new AssertionError(ex.getMessage());
        }
    }

    @Test
    public void setPlayerColorThrowsExWhenNullColor() {
        int gameID = getGameID("testGame");
        assertThrows(
                DataAccessException.class,
                () -> sqlGameDao.setPlayerColor(gameID, null, "testUser")
        );
    }

    @Test
    public void successClear() {
        // get connection
        try (var conn = getConnection()) {
        // start transaction
            try (var preparedStatement = conn.prepareStatement("START TRANSACTION;")) {
                preparedStatement.execute();
            }
        // clear db, assert no games
            sqlGameDao.clear();
            try (var preparedStatement = conn.prepareStatement("SELECT * FROM games")) {
                var resultSet = preparedStatement.executeQuery();
                assertFalse(resultSet.next(), "games still contains at least one row");
            }
        // rollback
            try (var preparedStatement = conn.prepareStatement("ROLLBACK;")) {
                preparedStatement.execute();
            }
        } catch (DataAccessException | SQLException ex) {
            throw new AssertionError(ex.getMessage());
        }
    }

    @AfterEach
    public void cleanup() {
        // manually delete test game, then delete test auth, then delete test user
        try (var conn = getConnection()) {
            String[] deleteStatements = {
                    "DELETE FROM games WHERE gameName = 'testGame'",
                    "DELETE FROM sessions WHERE authToken = '" + authToken + "'",
                    "DELETE FROM users WHERE username = 'testUser'"
            };
            for (String statement : deleteStatements) {
                try (var preparedStatement = conn.prepareStatement(statement)) {
                    preparedStatement.executeUpdate();
                }
            }
        } catch (DataAccessException | SQLException ex) {
            throw new AssertionError(ex.getMessage());
        }
    }
}
