package service;

import dataaccess.AuthDao;
import dataaccess.DataAccessException;
import dataaccess.GameDao;
import server.*;

import javax.xml.crypto.Data;

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
        if (!authDao.containsToken(authToken)) {
            return new ListResult(null, UNAUTHORIZED_ERR_MSG);
        }
        return new ListResult(gameDao.getGames(), null);
    }

    public CreateResult createGame(CreateRequest req, String authToken) {
        if (!authDao.containsToken(authToken)) {
            return new CreateResult(null, UNAUTHORIZED_ERR_MSG);
        }
        int gameID = gameDao.createGame(req.gameName());
        return new CreateResult(gameID, null);
    }

    public JoinResult joinGame(JoinRequest req, String authToken) {
        if (!authDao.containsToken(authToken)) {    // handles bad auth token
            return new JoinResult(UNAUTHORIZED_ERR_MSG);
        }
        if (req.gameID() == null || !gameDao.containsID(req.gameID())) {    // handles no id or bad id
            return new JoinResult(BAD_REQUEST_ERR_MSG);
        }
        String username = authDao.getUsername(authToken);
        try {   // normal working case
            gameDao.setPlayerColor(req.gameID(), req.playerColor(), username);
        } catch (DataAccessException ex) {  // handles case where color is taken by another user
            return new JoinResult(USER_TAKEN_ERR_MSG);
        } catch (NullPointerException ex) { // handles case where no color is given and thus color is null
            return new JoinResult(BAD_REQUEST_ERR_MSG);
        }
        return new JoinResult(null);
    }

    public void clear() {
        gameDao.clear();
        authDao.clear();
    }
}
