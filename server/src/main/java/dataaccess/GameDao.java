package dataaccess;

import model.GameData;

import java.util.Collection;

public interface GameDao {
    Collection<GameData> getGames();

    void clear();
}
