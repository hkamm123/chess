package dataaccess;

import model.AuthData;

public interface AuthDao {
    AuthData createAuth(String username) throws DataAccessException;

    String getUsername(String authToken) throws DataAccessException;

    boolean deleteAuth(String authToken);

    boolean containsToken(String authToken);

    void clear();
}
