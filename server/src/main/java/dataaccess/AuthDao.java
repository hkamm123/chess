package dataaccess;

import model.AuthData;

public interface AuthDao {
    AuthData createAuth(String username) throws DataAccessException;

    String getUsername(String authToken);

    boolean deleteAuth(String authToken);

    boolean containsToken(String authToken);

    void clear();
}
