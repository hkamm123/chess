package service;

import chess.ChessGame;
import chess.ChessMove;
import dataaccess.AuthDao;
import dataaccess.DataAccessException;
import dataaccess.GameDao;
import model.GameData;
import server.*;

import javax.xml.crypto.Data;

import java.util.Collection;

import static chess.ChessGame.TeamColor.BLACK;
import static chess.ChessGame.TeamColor.WHITE;
import static dataaccess.UserDao.BAD_REQUEST_ERR_MSG;
import static dataaccess.UserDao.USER_TAKEN_ERR_MSG;
import static service.UserService.UNAUTHORIZED_ERR_MSG;

public class GameService {
    private final GameDao gameDao;
    private final AuthDao authDao;

    public GameService(GameDao gameDao, AuthDao authDao) {
        this.gameDao = gameDao;
        this.authDao = authDao;
    }

    public ListResult listGames(String authToken) {
        try {
            if (!authDao.containsToken(authToken)) {
                return new ListResult(null, UNAUTHORIZED_ERR_MSG);
            }
            return new ListResult(gameDao.getGames(), null);
        } catch (DataAccessException ex) {
            return new ListResult(null, ex.getMessage());
        }
    }

    public CreateResult createGame(CreateRequest req, String authToken) {
        try {
            if (!authDao.containsToken(authToken)) {
                return new CreateResult(null, UNAUTHORIZED_ERR_MSG);
            }
            int gameID = gameDao.createGame(req.gameName());
            return new CreateResult(gameID, null);
        } catch (DataAccessException ex) {
            return new CreateResult(null, ex.getMessage());
        }
    }

    public JoinResult joinGame(JoinRequest req, String authToken) {
        try {
            if (!authDao.containsToken(authToken)) {    // handles bad auth token
                return new JoinResult(UNAUTHORIZED_ERR_MSG);
            }
            if (req.gameID() == null || !gameDao.containsID(req.gameID()) // no ID or bad ID
                    || req.playerColor() == null // null color
                    || req.playerColor() != WHITE) {
                if (req.playerColor() != BLACK) { // color not white or black
                    return new JoinResult(BAD_REQUEST_ERR_MSG);
                }
            }
        } catch (DataAccessException ex) {
            return new JoinResult(ex.getMessage());
        }
        try {   // normal working case
            String username = authDao.getUsername(authToken);
            gameDao.setPlayerColor(req.gameID(), req.playerColor(), username);
        } catch (DataAccessException ex) {  // handles case where color is taken by another user
            return new JoinResult(USER_TAKEN_ERR_MSG);
        } catch (NullPointerException ex) { // handles case where no color is given and thus color is null
            return new JoinResult(BAD_REQUEST_ERR_MSG);
        }
        return new JoinResult(null);
    }

    public void clear() throws DataAccessException {
        gameDao.clear();
        try {
            authDao.clear();
        } catch (DataAccessException ex) {
            throw new DataAccessException(ex.getMessage());
        }
    }

    public void removePlayerFromGame(String username, Integer gameID, String authToken) throws Exception {
        try {
            if (authDao.containsToken(authToken))
                gameDao.removePlayerFromGame(username, gameID);
        } catch (DataAccessException ex) {
            throw new Exception(ex.getMessage());
        }
    }

    public ChessGame makeMove(int gameID, String authToken, String username, ChessMove move) throws Exception {
        if (!authDao.containsToken(authToken)) {
            throw new Exception("unauthorized");
        }
        try {
            String whiteUsername = null;
            String blackUsername = null;
            ChessGame game = null;
            Collection<GameData> games = listGames(authToken).games();
            for (GameData gd : games) {
                if (gd.gameID() == gameID) {
                    game = gd.game();
                    whiteUsername = gd.whiteUsername();
                    blackUsername = gd.blackUsername();
                }
            }
            if (game == null) {
                throw new DataAccessException("Error: could not find requested game.");
            }
            ChessGame.TeamColor pieceColor = game.getBoard().getPiece(move.getStartPosition()).getTeamColor();
            if (pieceColor == WHITE && (whiteUsername == null || !whiteUsername.equals(username))) {
                throw new DataAccessException("Error: this user cannot move this piece.");
            }
            if (pieceColor == BLACK && (blackUsername == null || !blackUsername.equals(username))) {
                throw new DataAccessException("Error: this user cannot move this piece.");
            }
            return gameDao.makeMoveAndUpdate(gameID, move);
        } catch (DataAccessException ex) {
            throw new Exception(ex.getMessage());
        }
    }

    public String[] getUsernames(int gameID) throws Exception {
        try {
            return gameDao.getUsernames(gameID);
        } catch (DataAccessException ex) {
            throw new Exception(ex.getMessage());
        }
    }
}
