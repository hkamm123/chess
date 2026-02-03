package dataaccess;

import model.GameData;

import java.util.List;

public interface GameDao {
    List<GameData> getGames() throws DataAccessException;

    GameData getGame(int gameID) throws DataAccessException;

    int createGame(String gameName) throws DataAccessException;

    void updateGame(GameData gameData) throws DataAccessException;

    void clear() throws DataAccessException;
}
