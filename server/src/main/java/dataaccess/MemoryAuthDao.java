package dataaccess;

import model.AuthData;

import java.util.HashMap;
import java.util.Map;

public class MemoryAuthDao implements AuthDao{
    private final Map<String, String> sessions;

    public MemoryAuthDao() {
        sessions = new HashMap<>();
    }

    @Override
    public void createAuth(AuthData authData) throws DataAccessException {
        sessions.put(authData.authToken(), authData.username());
    }
}
