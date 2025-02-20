package dataaccess;

import model.AuthData;
import server.ClearResult;

import java.util.HashMap;

public class MemoryAuthDao implements AuthDao{
    private HashMap<String, String> auths = new HashMap<>();

    @Override
    public void createAuth(AuthData authData) {
        auths.put(authData.username(), authData.authToken());
    }

    @Override
    public String getAuthToken(String username) {
        return auths.get(username);
    }

    @Override
    public ClearResult clear() {
        this.auths = new HashMap<>();
        return new ClearResult(null);
    }
}
