package dataaccess;

import chess.ChessGame;
import model.GameData;

import java.util.Collection;

public interface GameDao {
    Collection<GameData> getGames() throws DataAccessException;

    //    returns the game ID
    int createGame(String gameName) throws DataAccessException;

    void clear() throws DataAccessException;

    boolean containsID(Integer id) throws DataAccessException;

    void setPlayerColor(Integer gameID, ChessGame.TeamColor playerColor, String username) throws DataAccessException;
}
