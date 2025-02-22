package service;

import dataaccess.*;
import model.AuthData;
import model.UserData;
import server.LoginRequest;
import server.LoginResult;
import server.RegisterRequest;
import server.RegisterResult;

import javax.xml.crypto.Data;
import java.util.UUID;

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

    public void clear() {
        userDao.clear();
        authDao.clear();
    }
}
