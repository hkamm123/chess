package service;

import dataaccess.AuthDao;
import dataaccess.DataAccessException;
import dataaccess.UserDao;
import model.AuthData;
import model.UserData;
import server.request.LoginRequest;
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

    public LoginResult register(RegisterRequest request) throws ServiceException {
        try {
            UserData userData = userDao.getUser(request.username());
            if (userData != null) {
                throw new ServiceException(ServiceException.ServiceExceptionType.ALREADY_TAKEN);
            }
            userDao.createUser(new UserData(request.username(), request.password(), request.email()));
            // TODO: hash user's password before saving
            String authToken = UUID.randomUUID().toString();
            authDao.createAuth(new AuthData(authToken, request.username()));
            return new LoginResult(request.username(), authToken);
        } catch (DataAccessException ex) {
            throw new ServiceException(ServiceException.ServiceExceptionType.SERVER_ERROR);
        }
    }

    public LoginResult login(LoginRequest request) throws ServiceException {
        try {
            UserData userData = userDao.getUser(request.username());
            if (userData == null || !validatePassword(request.password(), userData)) {
                throw new ServiceException(ServiceException.ServiceExceptionType.UNAUTHORIZED);
            }
            String authToken = UUID.randomUUID().toString();
            authDao.createAuth(new AuthData(authToken, request.username()));
            return new LoginResult(request.username(), authToken);
        } catch (DataAccessException ex) {
            throw new ServiceException(ServiceException.ServiceExceptionType.SERVER_ERROR);
        }
    }

    public void logout(String authToken) throws ServiceException {
        try {
            if (authDao.getAuth(authToken) == null) {
                throw new ServiceException(ServiceException.ServiceExceptionType.UNAUTHORIZED);
            }

            authDao.deleteAuth(authToken);
        } catch (DataAccessException ex) {
            throw new ServiceException(ServiceException.ServiceExceptionType.SERVER_ERROR);
        }
    }

    private boolean validatePassword(String requestPw, UserData userData) {
        return requestPw.equals(userData.password()); // TODO: change this to handle hashed passwords
    }
}
