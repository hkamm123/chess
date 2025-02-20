package service;

import dataaccess.*;
import model.AuthData;
import model.UserData;
import server.RegisterRequest;
import server.RegisterResult;

import javax.xml.crypto.Data;
import java.util.UUID;

import static dataaccess.UserDao.BAD_REQUEST_ERR_MSG;


public class UserService {
    private UserDao userDao = new MemoryUserDao();
    private AuthDao authDao = new MemoryAuthDao();
    public RegisterResult register(RegisterRequest req) throws DataAccessException {
        if (req.getUsername() == null || req.getPassword() == null || req.getEmail() == null) {
            return new RegisterResult(null, null, BAD_REQUEST_ERR_MSG);
        }
        UserData userData = new UserData(req.getUsername(), req.getPassword(), req.getEmail());
        try {
            userDao.createUser(userData);
            String authToken = UUID.randomUUID().toString();
            AuthData authData = new AuthData(authToken, userData.username());
            authDao.createAuth(authData);
            return new RegisterResult(userData.username(), authToken, null);
        } catch (DataAccessException ex) {
            return new RegisterResult(null, null, ex.getMessage());
        } catch (Exception ex) {
            return new RegisterResult(null, null, "Error: " + ex.getMessage());
        }
    }

    public void clear() {
        userDao.clear();
        authDao.clear();
    }
}
