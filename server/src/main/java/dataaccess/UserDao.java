package dataaccess;

import model.AuthData;
import model.UserData;

public interface UserDao {
    public static final String USER_TAKEN_ERR_MSG = "Error: already taken";

    public static final String BAD_REQUEST_ERR_MSG = "Error: bad request";

    UserData getUser(String username);

    void createUser(UserData userData) throws DataAccessException;
}
