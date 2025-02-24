package service;

import dataaccess.AuthDao;
import dataaccess.GameDao;
import server.AuthRequest;
import server.CreateRequest;
import server.CreateResult;
import server.ListResult;

import static service.UserService.UNAUTHORIZED_ERR_MSG;

public class GameService {
    private final GameDao gameDao;
    private final AuthDao authDao;

    public GameService(GameDao gameDao, AuthDao authDao) {
        this.gameDao = gameDao;
        this.authDao = authDao;
    }

    public ListResult listGames(AuthRequest req) {
        if (!authDao.containsToken(req.authToken())) {
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

    public void clear() {
        gameDao.clear();
    }
}
