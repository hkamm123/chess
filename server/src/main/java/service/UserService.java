package service;

import dataaccess.AuthDao;
import dataaccess.DataAccessException;
import dataaccess.UserDao;
import model.AuthData;
import model.UserData;
import server.request.RegisterRequest;
import server.result.LoginResult;

import java.util.UUID;

public class UserService {
    private final UserDao userDao;
    private final AuthDao authDao;

    public UserService(UserDao userDao, AuthDao authDao) {
        this.userDao = userDao;
        this.authDao = authDao;
    }

    public LoginResult register(RegisterRequest request) {
        try {
            UserData userData = userDao.getUser(request.username());
            if (userData != null) {
                // TODO: throw an AlreadyTakenException here
            }
            userDao.createUser(new UserData(request.username(), request.password(), request.email()));
            String authToken = UUID.randomUUID().toString();
            authDao.createAuth(new AuthData(authToken, request.username()));
            return new LoginResult(request.username(), authToken);
        } catch (DataAccessException ex) {
            // TODO: throw some type of exception here (will result in 500)
        }
    }
}
