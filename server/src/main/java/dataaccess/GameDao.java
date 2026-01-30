package dataaccess;

import model.GameData;

import java.util.List;

public interface GameDao {
    public List<GameData> getGames() throws DataAccessException;

    public GameData getGame(int gameID) throws DataAccessException;

    public int createGame(String gameName) throws DataAccessException;

    public void updateGame(GameData gameData) throws DataAccessException;

    public void clearGames() throws DataAccessException;
}
