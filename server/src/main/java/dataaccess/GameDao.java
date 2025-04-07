package dataaccess;

import chess.ChessGame;
import chess.ChessMove;
import model.GameData;

import java.util.Collection;

public interface GameDao {
    Collection<GameData> getGames() throws DataAccessException;

    //    returns the game ID
    int createGame(String gameName) throws DataAccessException;

    void clear() throws DataAccessException;

    boolean containsID(Integer id) throws DataAccessException;

    void setPlayerColor(Integer gameID, ChessGame.TeamColor playerColor, String username) throws DataAccessException;

    void removePlayerFromGame(String username, Integer gameID) throws DataAccessException;

    ChessGame makeMoveAndUpdate(int gameID, ChessMove move) throws DataAccessException;

    String[] getUsernames(int gameID) throws DataAccessException;

    void setGameToOver(Integer gameID) throws DataAccessException;
}
