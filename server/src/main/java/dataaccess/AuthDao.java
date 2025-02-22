package dataaccess;

import model.AuthData;
import server.ClearResult;

public interface AuthDao {
    AuthData createAuth(String username);

    String getAuthToken(String username);

    void clear();
}
