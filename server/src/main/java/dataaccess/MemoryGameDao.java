package dataaccess;

import chess.ChessGame;
import chess.ChessMove;
import model.GameData;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

// OBSOLETE CLASS - DO NOT USE
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
    public boolean containsID(Integer id) {
        for (GameData data : games) {
            if (data.gameID() == id) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void setPlayerColor(Integer gameID, ChessGame.TeamColor playerColor, String username) throws DataAccessException {
        Iterator<GameData> itr = games.iterator();
        GameData gameToUpdate = null;
        while (itr.hasNext()) {
            GameData data = itr.next();
            if (data.gameID() == gameID) {
                gameToUpdate = data;
//                remove the game to add a new one since GameData is an immutable record
                itr.remove();
                break;
            }
        }
        switch (playerColor) {
            case BLACK:
                if (gameToUpdate.blackUsername() != null) {
                    throw new DataAccessException("bad request: color already taken");
                }
                games.add(new GameData(gameToUpdate.gameID(),
                        gameToUpdate.whiteUsername(),
                        username,
                        gameToUpdate.gameName(),
                        gameToUpdate.game()));
                break;
            case WHITE:
                if (gameToUpdate.whiteUsername() != null) {
                    throw new DataAccessException("bad request: color already taken");
                }
                games.add(new GameData(gameToUpdate.gameID(),
                        username,
                        gameToUpdate.blackUsername(),
                        gameToUpdate.gameName(),
                        gameToUpdate.game()));
        }
    }

    @Override
    public void removePlayerFromGame(String username, Integer gameID) throws DataAccessException {

    }

    @Override
    public ChessGame makeMoveAndUpdate(int gameID, ChessMove move) {
        return null;
    }

    @Override
    public String[] getUsernames(int gameID) {
        return new String[0];
    }

    @Override
    public void clear() {
        games = new ArrayList<>();
        nextID = 1;
    }
}
