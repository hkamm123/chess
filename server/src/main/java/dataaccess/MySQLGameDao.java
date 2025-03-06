package dataaccess;

import chess.ChessGame;
import model.GameData;

import java.util.Collection;
import java.util.List;

public class MySQLGameDao implements GameDao {
    public MySQLGameDao() throws DataAccessException {
        DatabaseManager.configureDatabase();
    }

    @Override
    public Collection<GameData> getGames() {
        return List.of();
    }

    @Override
    public int createGame(String gameName) {
        return 0;
    }

    @Override
    public void clear() {

    }

    @Override
    public boolean containsID(Integer id) {
        return false;
    }

    @Override
    public void setPlayerColor(Integer gameID, ChessGame.TeamColor playerColor, String username) throws DataAccessException {

    }
}
