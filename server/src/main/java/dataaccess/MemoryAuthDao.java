package dataaccess;

import model.AuthData;
import server.ClearResult;

import java.util.HashMap;
import java.util.UUID;

public class MemoryAuthDao implements AuthDao {
    private HashMap<String, String> auths = new HashMap<>();

    @Override
    public AuthData createAuth(String username) {
        AuthData authData = new AuthData(UUID.randomUUID().toString(), username);
        auths.put(authData.username(), authData.authToken());
        return authData;
    }

    @Override
    public String getAuthToken(String username) {
        return auths.get(username);
    }

    @Override
    public void clear() {
        this.auths = new HashMap<>();
    }
}
