package dataaccess;

import model.UserData;

public interface UserDao {
    UserData getUser(String username) throws DataAccessException;

    void createUser(UserData userData) throws DataAccessException;

    void clear() throws DataAccessException;
}
