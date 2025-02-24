package dataaccess;

import model.GameData;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class MemoryGameDao implements GameDao {
    private Collection<GameData> games = new ArrayList<>();

    @Override
    public Collection<GameData> getGames() {
        return games;
    }

    @Override
    public void clear() {
        games = new ArrayList<>();
    }
}
