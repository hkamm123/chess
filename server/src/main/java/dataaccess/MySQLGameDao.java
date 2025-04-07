package dataaccess;

import chess.ChessGame;
import chess.ChessMove;
import chess.InvalidMoveException;
import com.google.gson.Gson;
import model.GameData;

import java.io.IOException;
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
                if (resultSet.next()) {
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
                while (resultSet.next()) {
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
                if (resultSet.next()) {
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
        int userID = getUserID(username);
        if (playerColor == BLACK) {
            column = "blackUserID";
        } else if (playerColor == WHITE) {
            column = "whiteUserID";
        }
        try (var conn = getConnection()) {
            String queryStatement = "SELECT * FROM games WHERE " + column + " IS NULL AND gameID = " + gameID;
            try (var preparedStatement = conn.prepareStatement(queryStatement)) {
                var resultSet = preparedStatement.executeQuery();
                if (!resultSet.next()) {
                    throw new DataAccessException("color already taken");
                }
            }

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
    public void removePlayerFromGame(String username, Integer gameID) throws DataAccessException {
        try (var conn = getConnection()) {
            int[] userIDs = getUserIDsFromGame(gameID);

            // get the userID based on the given username
            int userID = 0;
            try (var preparedStatement = conn.prepareStatement("SELECT userID FROM users WHERE username = ?")) {
                preparedStatement.setString(1, username);
                var resultSet = preparedStatement.executeQuery();
                if (resultSet.next()) {
                    userID = resultSet.getInt(1);
                }
            }

            // if the white spot in the game is taken, set the whiteUserID to null if it matches the given user's userID
            if (userIDs[0] != 0) {
                String updateStatement = "UPDATE games SET whiteUserID = NULL WHERE whiteUserID = ? AND gameID = ?";
                try (var preparedStatement = conn.prepareStatement(updateStatement)) {
                    preparedStatement.setInt(1, userID);
                    preparedStatement.setInt(2, gameID);
                    preparedStatement.executeUpdate();
                }
            }

            // same as above but with black
            if (userIDs[1] != 0) {
                String updateStatement = "UPDATE games SET blackUserID = NULL WHERE blackUserID = ? AND gameID = ?";
                try (var preparedStatement = conn.prepareStatement(updateStatement)) {
                    preparedStatement.setInt(1, userID);
                    preparedStatement.setInt(2, gameID);
                    preparedStatement.executeUpdate();
                }
            }
        } catch (DataAccessException | SQLException ex) {
            throw new DataAccessException("Error: " + ex.getMessage());
        }
    }

    @Override
    public ChessGame makeMoveAndUpdate(int gameID, ChessMove move) throws DataAccessException {
        try (var conn = getConnection()) {
            ChessGame game = getGameByID(gameID);
            try {
                game.makeMove(move);
            } catch (InvalidMoveException ex) {
                throw new DataAccessException("Ope! Looks like that move is not valid.");
            }

            updateGame(gameID, game);
            return game;
        } catch (DataAccessException | SQLException ex) {
            throw new DataAccessException(ex.getMessage());
        }
    }

    @Override
    public String[] getUsernames(int gameID) throws DataAccessException {
        try (var conn = getConnection()) {
            int[] userIDs = getUserIDsFromGame(gameID);
            String[] usernames = new String[]{null, null};

            String queryStatement = "SELECT username FROM users WHERE userID = ?";
            // finding the white username
            try (var preparedStatement = conn.prepareStatement(queryStatement)) {
                preparedStatement.setInt(1, userIDs[0]);
                var resultSet = preparedStatement.executeQuery();
                if (resultSet.next()) {
                    usernames[0] = resultSet.getString(1);
                }
            }

            // finding the black username
            try (var preparedStatement = conn.prepareStatement(queryStatement)) {
                preparedStatement.setInt(1, userIDs[1]);
                var resultSet = preparedStatement.executeQuery();
                if (resultSet.next()) {
                    usernames[1] = resultSet.getString(1);
                }
            }
            return usernames;
        } catch (DataAccessException | SQLException ex) {
            throw new DataAccessException(ex.getMessage());
        }
    }

    @Override
    public void setGameToOver(Integer gameID) throws DataAccessException {
        ChessGame game = getGameByID(gameID);
        if (game.isOver()) {
            throw new DataAccessException("Game is already over.");
        } else {
            game.setGameOverFlag(true);
            updateGame(gameID, game);
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

    private ChessGame getGameByID(int gameID) throws DataAccessException {
        try (var conn = getConnection()) {
            String gameJson = null;
            String queryStatement = "SELECT chessGameJson FROM games WHERE gameID = ?";
            try (var preparedStatement = conn.prepareStatement(queryStatement)) {
                preparedStatement.setInt(1, gameID);
                var resultSet = preparedStatement.executeQuery();
                if (resultSet.next()) {
                    gameJson = resultSet.getString(1);
                }
                if (gameJson == null) {
                    throw new DataAccessException("Could not find game in database.");
                }
            }

            return serializer.fromJson(gameJson, ChessGame.class);
        } catch (Exception ex) {
            throw new DataAccessException(ex.getMessage());
        }
    }

    private void updateGame(int gameID, ChessGame game) throws DataAccessException {
        try (var conn = getConnection()) {
            String updateStatement = "UPDATE games SET chessGameJson = ? WHERE gameID = ?";
            try (var preparedStatement = conn.prepareStatement(updateStatement)) {
                preparedStatement.setString(1, serializer.toJson(game));
                preparedStatement.setInt(2, gameID);
                preparedStatement.executeUpdate();
            }
        } catch (Exception ex) {
            throw new DataAccessException(ex.getMessage());
        }
    }

    private int[] getUserIDsFromGame(int gameID) throws DataAccessException {
        try (var conn = getConnection()) {
            int[] userIDs = new int[]{0,0};
            // get the whiteUserID from the selected game
            String queryStatement = "SELECT whiteUserID FROM games WHERE " + "gameID = " + gameID;
            try (var preparedStatement = conn.prepareStatement(queryStatement)) {
                var resultSet = preparedStatement.executeQuery();
                if (resultSet.next()) {
                    userIDs[0] = resultSet.getInt(1);
                }
            }

            // get the blackUserID from the selected game
            queryStatement = queryStatement.replace("whiteUserID", "blackUserID");
            try (var preparedStatement = conn.prepareStatement(queryStatement)) {
                var resultSet = preparedStatement.executeQuery();
                if (resultSet.next()) {
                    userIDs[1] = resultSet.getInt(1);
                }
            }

            return userIDs;
        } catch (DataAccessException | SQLException ex) {
            throw new DataAccessException(ex.getMessage());
        }
    }
}
