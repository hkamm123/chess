package dataaccess;

import chess.ChessGame;
import model.GameData;

import java.util.Collection;

public interface GameDao {
    Collection<GameData> getGames();

    //    returns the game ID
    int createGame(String gameName);

    void clear();

    boolean containsID(Integer id);

    void setPlayerColor(Integer gameID, ChessGame.TeamColor playerColor, String username) throws DataAccessException;
}
