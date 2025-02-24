package service;

import dataaccess.*;
import model.AuthData;
import model.UserData;
import server.*;

import static dataaccess.UserDao.BAD_REQUEST_ERR_MSG;


public class UserService {
    private UserDao userDao;
    private AuthDao authDao;

    public UserService(UserDao userDao, AuthDao authDao) {
        this.userDao = userDao;
        this.authDao = authDao;
    }

    public RegisterResult register(RegisterRequest req) throws DataAccessException {
        if (req.username() == null || req.password() == null || req.email() == null) {
            return new RegisterResult(null, null, BAD_REQUEST_ERR_MSG);
        }
        UserData userData = new UserData(req.username(), req.password(), req.email());
        try {
            userDao.createUser(userData);
            return login(new LoginRequest(userData.username(), userData.password()));
        } catch (DataAccessException ex) {
            return new RegisterResult(null, null, ex.getMessage());
        } catch (Exception ex) {
            return new RegisterResult(null, null, "Error: " + ex.getMessage());
        }
    }

    public RegisterResult login(LoginRequest req) {
        if (!userDao.isValidCredentials(req.username(), req.password())) {
            return new RegisterResult(null, null, "Error: unauthorized");
        }
        try {
            AuthData authData = authDao.createAuth(req.username());
            return new RegisterResult(authData.username(), authData.authToken(), null);
        } catch (Exception ex) {
            return new RegisterResult(null, null, "Error: " + ex.getMessage());
        }
    }

    public LogoutResult logout(LogoutRequest req) {
        boolean authDeleted = false;
        try {
            authDeleted = authDao.deleteAuth(req.authToken());
        } catch (Exception ex) {
            return new LogoutResult("Error: " + ex.getMessage());
        }
        if (authDeleted) {
            return new LogoutResult(null);
        } else {
            return new LogoutResult("Error: unauthorized");
        }
    }

    public void clear() {
        userDao.clear();
        authDao.clear();
    }
}
