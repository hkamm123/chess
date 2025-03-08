package dataaccess;

import model.AuthData;
import model.UserData;

public interface UserDao {
    public static final String USER_TAKEN_ERR_MSG = "Error: already taken";

    public static final String BAD_REQUEST_ERR_MSG = "Error: bad request";

    UserData getUser(String username) throws DataAccessException;

    void createUser(UserData userData) throws DataAccessException;

    boolean isValidCredentials(String username, String password) throws DataAccessException;

    void clear();
}
