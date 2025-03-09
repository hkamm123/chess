package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import model.GameData;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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
    public int createGame(String gameName) {
        return 0;
    }

    @Override
    public boolean containsID(Integer id) {
        return false;
    }

    @Override
    public void setPlayerColor(Integer gameID, ChessGame.TeamColor playerColor, String username) throws DataAccessException {

    }

    @Override
    public void clear() {

    }
}
