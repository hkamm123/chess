package service;

import dataaccess.*;
import model.AuthData;
import model.UserData;
import org.mindrot.jbcrypt.BCrypt;
import server.*;

import static dataaccess.UserDao.BAD_REQUEST_ERR_MSG;
import static dataaccess.UserDao.USER_TAKEN_ERR_MSG;

public class UserService {
    private UserDao userDao;
    private AuthDao authDao;
    public static final String UNAUTHORIZED_ERR_MSG = "Error: unauthorized";

    public UserService(UserDao userDao, AuthDao authDao) {
        this.userDao = userDao;
        this.authDao = authDao;
    }

    public RegisterResult register(RegisterRequest req) throws DataAccessException {
        if (req.username() == null || req.password() == null || req.email() == null) {
            return new RegisterResult(null, null, BAD_REQUEST_ERR_MSG);
        }
        if (userDao.getUser(req.username()) != null) { // if user already exists
            return new RegisterResult(null, null, USER_TAKEN_ERR_MSG);
        }
        String hashedPassword = BCrypt.hashpw(req.password(), BCrypt.gensalt()); // hash the given password for storage
        UserData userData = new UserData(req.username(), hashedPassword, req.email());
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
        boolean authorized = false;
        try {
            authorized = userDao.isValidCredentials(req.username(), req.password());
        } catch (DataAccessException ex) {
            return new RegisterResult(null, null, ex.getMessage());
        }
        if (!authorized) {
            return new RegisterResult(null, null, UNAUTHORIZED_ERR_MSG);
        }
        try {
            AuthData authData = authDao.createAuth(req.username());
            return new RegisterResult(authData.username(), authData.authToken(), null);
        } catch (Exception ex) {
            return new RegisterResult(null, null, "Error: " + ex.getMessage());
        }
    }

    public LogoutResult logout(String authToken) {
        boolean authDeleted = false;
        try {
            authDeleted = authDao.deleteAuth(authToken);
        } catch (Exception ex) {
            return new LogoutResult("Error: " + ex.getMessage());
        }
        if (authDeleted) {
            return new LogoutResult(null);
        } else {
            return new LogoutResult(UNAUTHORIZED_ERR_MSG);
        }
    }

    public void clear() throws DataAccessException {
        try {
            userDao.clear();
        } catch (DataAccessException ex) {
            throw new DataAccessException(ex.getMessage());
        }
        authDao.clear();
    }
}
