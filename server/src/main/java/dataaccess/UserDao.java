package dataaccess;

import model.AuthData;
import model.UserData;

public interface UserDao {
    public static final String USER_TAKEN_ERR_MSG = "Username already taken.";

    UserData getUser(String username);

    void createUser(UserData userData) throws DataAccessException;

    void createAuth(AuthData authData);
}
