package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import model.GameData;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static chess.ChessGame.TeamColor.BLACK;
import static chess.ChessGame.TeamColor.WHITE;
import static dataaccess.DatabaseManager.getConnection;

public class MySQLGameDao implements GameDao {
    private Gson serializer;

    public MySQLGameDao() throws DataAccessException {
        DatabaseManager.configureDatabase();
        serializer = new Gson();
    }

    private String getUserName(int userID) throws DataAccessException {
        try (var conn = getConnection()) {
            String queryStatement = "SELECT username FROM users WHERE userID = " + userID;
            try (var preparedStatement = conn.prepareStatement(queryStatement)) {
                var resultSet = preparedStatement.executeQuery();
                if(resultSet.next()) {
                    return resultSet.getString("username");
                } else {
                    return null;
                }
            }
        } catch (DataAccessException | SQLException ex) {
            throw new DataAccessException(ex.getMessage());
        }
    }

    private GameData convertToGameData(
            int gameID,
            int whiteUserID,
            int blackUserID,
            String gameName,
            String chessGameJson) throws DataAccessException {
        try {
            String whiteUsername = getUserName(whiteUserID);
            String blackUsername = getUserName(blackUserID);
            ChessGame game = serializer.fromJson(chessGameJson, ChessGame.class);
            return new GameData(gameID, whiteUsername, blackUsername, gameName, game);
        } catch (DataAccessException ex) {
            throw new DataAccessException(ex.getMessage());
        }
    }

    @Override
    public Collection<GameData> getGames() throws DataAccessException {
        Collection<GameData> output = new ArrayList<>();
        try (var conn = getConnection()) {
            String queryStatement = "SELECT * FROM games";
            try (var preparedStatement = conn.prepareStatement(queryStatement)) {
                var resultSet = preparedStatement.executeQuery();
                while(resultSet.next()) {
                    int gameID = resultSet.getInt("gameID");
                    int whiteUserID = resultSet.getInt("whiteUserID");
                    int blackUserID = resultSet.getInt("blackUserID");
                    String gameName = resultSet.getString("gameName");
                    String chessGameJson = resultSet.getString("chessGameJson");
                    output.add(convertToGameData(gameID, whiteUserID, blackUserID, gameName, chessGameJson));
                }
                return output;
            }
        } catch (DataAccessException | SQLException ex) {
            throw new DataAccessException("Error: " + ex.getMessage());
        }
    }

    @Override
    public int createGame(String gameName) throws DataAccessException {
        try (var conn = getConnection()) {
            String json = serializer.toJson(new ChessGame());
            String createStatement = """
                    INSERT INTO games (gameName, chessGameJson) VALUES (?, ?)
                    """;
            try (var preparedStatement = conn.prepareStatement(createStatement)) {
                preparedStatement.setString(1, gameName);
                preparedStatement.setString(2, json);
                preparedStatement.executeUpdate();
            }

            String getIDStatement = """
                    SELECT (gameID) FROM games WHERE gameName = ?
                    """;
            try (var preparedStatement = conn.prepareStatement(getIDStatement)) {
                preparedStatement.setString(1, gameName);
                var resultSet = preparedStatement.executeQuery();
                resultSet.next();
                return resultSet.getInt("gameID");
            }
        } catch (DataAccessException | SQLException ex) {
            throw new DataAccessException("Error: " + ex.getMessage());
        }
    }

    @Override
    public boolean containsID(Integer id) throws DataAccessException {
        if (id == null) {
            throw new DataAccessException("Error: cannot check database for a null gameID");
        }
        try (var conn = getConnection()) {
            String queryStatement = "SELECT * FROM games WHERE gameID = " + id;
            try (var preparedStatement = conn.prepareStatement(queryStatement)) {
                var resultSet = preparedStatement.executeQuery();
                if (resultSet.next()) {
                    return true;
                } else {
                    return false;
                }
            }
        } catch (DataAccessException | SQLException ex) {
            throw new DataAccessException("Error: " + ex.getMessage());
        }
    }

    private int getUserID(String username) throws DataAccessException {
        try (var conn = getConnection()) {
            String queryStatement = "SELECT userID FROM users WHERE username = '" + username + "'";
            try (var preparedStatement = conn.prepareStatement(queryStatement)) {
                var resultSet = preparedStatement.executeQuery();
                if(resultSet.next()) {
                    return resultSet.getInt("userID");
                } else {
                    throw new DataAccessException("userID not found");
                }
            }
        } catch (DataAccessException | SQLException ex) {
            throw new DataAccessException(ex.getMessage());
        }
    }

    @Override
    public void setPlayerColor(
            Integer gameID,
            ChessGame.TeamColor playerColor,
            String username) throws DataAccessException {
        String column = "";
        int userID = getUserID("testUser");
        if (playerColor == BLACK) {
            column = "blackUserID";
        } else if (playerColor == WHITE) {
            column = "whiteUserID";
        }
        try (var conn = getConnection()) {
            String updateStatement = "UPDATE games SET " + column + " = ? WHERE gameID = ?";
            try (var preparedStatement = conn.prepareStatement(updateStatement)) {
                preparedStatement.setInt(1, userID);
                preparedStatement.setInt(2, gameID);
                preparedStatement.executeUpdate();
            }
        } catch (DataAccessException | SQLException ex) {
            throw new DataAccessException("Error: " + ex.getMessage());
        }
    }

    @Override
    public void clear() throws DataAccessException {
        try (var preparedStatement = getConnection().prepareStatement("DELETE FROM games")) {
            preparedStatement.execute();
        } catch (DataAccessException | SQLException ex) {
            throw new DataAccessException("Error: " + ex.getMessage());
        }
    }
}
