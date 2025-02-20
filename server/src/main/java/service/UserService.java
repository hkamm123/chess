package service;

import dataaccess.DataAccessException;
import dataaccess.MemoryUserDao;
import dataaccess.UserDao;
import model.AuthData;
import model.UserData;
import server.RegisterRequest;
import server.RegisterResult;

import javax.xml.crypto.Data;
import java.util.UUID;

import static dataaccess.UserDao.BAD_REQUEST_ERR_MSG;


public class UserService {
    private UserDao dao = new MemoryUserDao();
    public RegisterResult register(RegisterRequest req) throws DataAccessException {
        if (req.getUsername() == null || req.getPassword() == null || req.getEmail() == null) {
            return new RegisterResult(null, null, BAD_REQUEST_ERR_MSG);
        }
        UserData userData = new UserData(req.getUsername(), req.getPassword(), req.getEmail());
        try {
            dao.createUser(userData);
            String authToken = UUID.randomUUID().toString();
            AuthData authData = new AuthData(authToken, userData.username());
            dao.createAuth(authData);
            return new RegisterResult(userData.username(), authToken, null);
        } catch (DataAccessException ex) {
            return new RegisterResult(null, null, ex.getMessage());
        } catch (Exception ex) {
            return new RegisterResult(null, null, "Error: " + ex.getMessage());
        }
    }
}
