package service;

import chess.ChessGame;
import dataaccess.AuthDao;
import dataaccess.GameDao;
import dataaccess.UserDao;
import server.AuthRequest;
import server.ListResult;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static service.UserService.UNAUTHORIZED_ERR_MSG;

public class GameService {
    private GameDao gameDao;
    private AuthDao authDao;

    public GameService(GameDao gameDao, AuthDao authDao) {
        this.gameDao = gameDao;
        this.authDao = authDao;
    }

    public ListResult listGames(AuthRequest req) {
        if (!authDao.contains(req.authToken())) {
            return new ListResult(null, UNAUTHORIZED_ERR_MSG);
        }
        return new ListResult(gameDao.getGames(), null);
    }

    public void clear() {
        gameDao.clear();
    }
}
