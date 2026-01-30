package dataaccess;

import model.AuthData;

public interface AuthDao {
    void createAuth(AuthData authData) throws DataAccessException;
}
