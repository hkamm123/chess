package dataaccess;

import chess.ChessGame;
import model.GameData;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class MemoryGameDao implements GameDao {
    private Collection<GameData> games = new ArrayList<>();
    private int nextID = 1;

    @Override
    public Collection<GameData> getGames() {
        return games;
    }

    @Override
    public int createGame(String gameName) {
        games.add(new GameData(nextID, null, null, gameName, new ChessGame()));
        return nextID++;
    }

    @Override
    public void clear() {
        games = new ArrayList<>();
        nextID = 1;
    }
}
