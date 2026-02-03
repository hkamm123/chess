package dataaccess;

import model.AuthData;

import java.util.HashMap;
import java.util.Map;

public class MemoryAuthDao implements AuthDao{
    private Map<String, AuthData> sessions;

    public MemoryAuthDao() {
        sessions = new HashMap<>();
    }

    @Override
    public void createAuth(AuthData authData) throws DataAccessException {
        sessions.put(authData.authToken(), authData);
    }

    @Override
    public AuthData getAuth(String authToken) throws DataAccessException {
        return sessions.get(authToken);
    }

    @Override
    public void deleteAuth(String authToken) throws DataAccessException {
        sessions.remove(authToken);
    }

    @Override
    public void clear() throws DataAccessException {
        sessions = new HashMap<>();
    }
}
