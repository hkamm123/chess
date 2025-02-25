package dataaccess;

import model.AuthData;
import server.ClearResult;

public interface AuthDao {
    AuthData createAuth(String username);

    String getAuthToken(String username);

    String getUsername(String authToken);

    boolean deleteAuth(String authToken);

    boolean containsToken(String authToken);

    void clear();
}
