package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import model.GameData;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import static dataaccess.DatabaseManager.*;

public class MySQLGameDao implements GameDao {
    public MySQLGameDao() throws DataAccessException {
       createTables();
    }

    @Override
    public List<GameData> getGames() throws DataAccessException {
        List<GameData> games = new ArrayList<>();
        try (var conn = getConnection(); var statement = conn.prepareStatement("SELECT * FROM games")) {
            var resultSet = statement.executeQuery();
            while(resultSet.next()) {
                ChessGame game = new Gson().fromJson(resultSet.getString("game"), ChessGame.class);
                games.add(new GameData(
                        resultSet.getInt("gameID"),
                        resultSet.getString("whiteUsername"),
                        resultSet.getString("blackUsername"),
                        resultSet.getString("gameName"),
                        game)
                );
            }
            return games;
        } catch (SQLException ex) {
            throw new DataAccessException(ex.getMessage());
        }
    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        return null;
    }

    @Override
    public int createGame(String gameName) throws DataAccessException {
        return 0;
    }

    @Override
    public void updateGame(GameData gameData) throws DataAccessException {

    }

    @Override
    public void clear() throws DataAccessException {

    }
}
