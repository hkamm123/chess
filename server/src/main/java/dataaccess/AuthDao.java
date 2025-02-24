package dataaccess;

import model.AuthData;
import server.ClearResult;

public interface AuthDao {
    AuthData createAuth(String username);

    String getAuthToken(String username);

    boolean deleteAuth(String authToken);

    boolean contains(String authToken);

    void clear();
}
