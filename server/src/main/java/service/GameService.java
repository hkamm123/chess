package service;

import dataaccess.AuthDao;
import dataaccess.DataAccessException;
import dataaccess.GameDao;
import model.AuthData;
import model.GameData;
import server.request.CreateRequest;
import server.request.JoinRequest;
import server.result.CreateResult;
import server.result.ListGamesResult;

import java.util.List;

import static chess.ChessGame.TeamColor.BLACK;
import static chess.ChessGame.TeamColor.WHITE;

public class GameService {
    private final GameDao gameDao;
    private final AuthDao authDao;

    public GameService(GameDao gameDao, AuthDao authDao) {
        this.gameDao = gameDao;
        this.authDao = authDao;
    }

    public ListGamesResult listGames(String authToken) throws ServiceException {
        try {
            validateAuthToken(authToken);
            List<GameData> games = gameDao.getGames();
            return new ListGamesResult(games);
        } catch (DataAccessException ex) {
            throw new ServiceException(ServiceException.ServiceExceptionType.SERVER_ERROR);
        }
    }

    public CreateResult createGame(String authToken, CreateRequest request) throws ServiceException {
        try {
            validateAuthToken(authToken);
            int gameID = gameDao.createGame(request.gameName());
            return new CreateResult(gameID);
        } catch (DataAccessException ex) {
            throw new ServiceException(ServiceException.ServiceExceptionType.SERVER_ERROR);
        }
    }

    public void joinGame(String authToken, JoinRequest request) throws ServiceException {
        try {
            AuthData auth = authDao.getAuth(authToken);
            if (auth == null) {
                throw new ServiceException(ServiceException.ServiceExceptionType.UNAUTHORIZED);
            }
            GameData game = gameDao.getGame(request.gameID());
            if (game == null) {
                throw new ServiceException(ServiceException.ServiceExceptionType.BAD_REQUEST);
            }
            if ((request.playerColor() == WHITE && game.whiteUsername() != null) ||
                    (request.playerColor() == BLACK && game.blackUsername() != null)) {
                throw new ServiceException(ServiceException.ServiceExceptionType.ALREADY_TAKEN);
            }
            GameData newGame = getUpdateGameData(request, game, auth); // sets updated username (see below)
            gameDao.updateGame(newGame);
        } catch (DataAccessException ex) {
            throw new ServiceException(ServiceException.ServiceExceptionType.SERVER_ERROR);
        }
    }

    private void validateAuthToken(String authToken) throws ServiceException {
        try {
            if (authDao.getAuth(authToken) == null) {
                throw new ServiceException(ServiceException.ServiceExceptionType.UNAUTHORIZED);
            }
        } catch (DataAccessException ex) {
            throw new ServiceException(ServiceException.ServiceExceptionType.SERVER_ERROR);
        }
    }

    private GameData getUpdateGameData(JoinRequest request, GameData game, AuthData auth) {
        GameData newGame;
        if (request.playerColor() == WHITE) {
            newGame = new GameData(
                    game.gameID(),
                    auth.username(),
                    game.blackUsername(),
                    game.gameName(),
                    game.game()
            );
        } else {
            newGame = new GameData(
                    game.gameID(),
                    game.whiteUsername(),
                    auth.username(),
                    game.gameName(),
                    game.game()
            );
        }
        return newGame;
    }
}
