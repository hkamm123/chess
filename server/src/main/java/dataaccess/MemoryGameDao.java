package dataaccess;

import chess.ChessGame;
import model.GameData;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MemoryGameDao implements GameDao {
    private Map<Integer, GameData> games;
    private int currentID;

    public MemoryGameDao() {
        this.games = new ConcurrentHashMap<>();
        currentID = 1;
    }

    @Override
    public List<GameData> getGames() throws DataAccessException {
        return new ArrayList<>(games.values());
    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        return games.get(gameID);
    }

    @Override
    public int createGame(String gameName) throws DataAccessException {
        int id = currentID++;
        games.put(id, new GameData(id, null, null, gameName, new ChessGame()));
        return id;
    }

    @Override
    public void updateGame(GameData gameData) throws DataAccessException {
        games.put(gameData.gameID(), gameData);
    }

    @Override
    public void clearGames() throws DataAccessException {
        games = new ConcurrentHashMap<>();
    }
}
