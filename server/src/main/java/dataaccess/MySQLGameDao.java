package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import model.GameData;

import java.sql.SQLException;
import java.sql.Statement;
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
        try (var conn = getConnection(); var statement = conn.prepareStatement("SELECT * FROM games WHERE gameID = ?")) {
            statement.setInt(1, gameID);
            var resultSet = statement.executeQuery();
            if (resultSet.next()) {
               ChessGame game = new Gson().fromJson(resultSet.getString("game"), ChessGame.class);
               return new GameData(
                       resultSet.getInt("gameID"),
                       resultSet.getString("whiteUsername"),
                       resultSet.getString("blackUsername"),
                       resultSet.getString("gameName"),
                       game
               );
            } else {
                return null;
            }
        } catch(SQLException ex) {
            throw new DataAccessException(ex.getMessage());
        }
    }

    @Override
    public int createGame(String gameName) throws DataAccessException {
        try (
                var conn = getConnection();
                var statement = conn.prepareStatement(
                        """
                        INSERT INTO games (whiteUsername, blackUsername, gameName, game) VALUES (NULL, NULL, ?, ?)
                        """,
                        Statement.RETURN_GENERATED_KEYS
                )) {
            statement.setString(1, gameName);
            statement.setString(2, new Gson().toJson(new ChessGame()));
            statement.executeUpdate();
            var resultSet = statement.getGeneratedKeys();
            if (resultSet.next()) {
                return resultSet.getInt(1);
            } else {
                throw new DataAccessException("game was not created");
            }
        } catch (SQLException ex) {
            throw new DataAccessException(ex.getMessage());
        }
    }

    @Override
    public void updateGame(GameData gameData) throws DataAccessException {
        try (var conn = getConnection(); var statement = conn.prepareStatement("""
                UPDATE games SET whiteUsername = ?, blackUsername = ?, game = ? WHERE gameID = ?
                """)) {
            statement.setString(1, gameData.whiteUsername());
            statement.setString(2, gameData.blackUsername());
            statement.setString(3, new Gson().toJson(gameData.game()));
            statement.setInt(4, gameData.gameID());
            statement.executeUpdate();
        } catch (SQLException ex) {
            throw new DataAccessException(ex.getMessage());
        }
    }

    @Override
    public void clear() throws DataAccessException {
        try (var conn = getConnection(); var statement = conn.prepareStatement("TRUNCATE TABLE games")) {
            statement.execute();
        } catch (SQLException ex) {
            throw new DataAccessException(ex.getMessage());
        }
    }
}
