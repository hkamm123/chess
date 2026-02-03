package service;

import dataaccess.AuthDao;
import dataaccess.DataAccessException;
import dataaccess.GameDao;
import model.GameData;
import server.request.CreateRequest;
import server.result.CreateResult;
import server.result.ListGamesResult;

import java.util.List;

public class GameService {
    private final GameDao gameDao;
    private final AuthDao authDao;

    public GameService(GameDao gameDao, AuthDao authDao) {
        this.gameDao = gameDao;
        this.authDao = authDao;
    }

    public ListGamesResult listGames(String authToken) throws ServiceException {
        try {
            if (authDao.getAuth(authToken) == null) {
                throw new ServiceException(ServiceException.ServiceExceptionType.UNAUTHORIZED);
            }
            List<GameData> games = gameDao.getGames();
            return new ListGamesResult(games);
        } catch (DataAccessException ex) {
            throw new ServiceException(ServiceException.ServiceExceptionType.SERVER_ERROR);
        }
    }

    public CreateResult createGame(String authToken, CreateRequest request) throws ServiceException {
        try {
            if (authDao.getAuth(authToken) == null) {
                throw new ServiceException(ServiceException.ServiceExceptionType.UNAUTHORIZED);
            }
            int gameID = gameDao.createGame(request.gameName());
            return new CreateResult(gameID);
        } catch (DataAccessException ex) {
            throw new ServiceException(ServiceException.ServiceExceptionType.SERVER_ERROR);
        }
    }
}
