package dataaccess;

import model.GameData;

import java.util.Collection;

public interface GameDao {
    Collection<GameData> getGames();

//    returns the game ID
    int createGame(String gameName);

    void clear();
}
