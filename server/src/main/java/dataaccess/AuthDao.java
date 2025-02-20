package dataaccess;

import model.AuthData;
import server.ClearResult;

public interface AuthDao {
    void createAuth(AuthData authData);

    String getAuthToken(String username);

    void clear();
}
