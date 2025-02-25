package dataaccess;

import model.AuthData;
import server.ClearResult;

import java.util.*;

public class MemoryAuthDao implements AuthDao {
    //    private HashMap<String, String> auths = new HashMap<>();
    private Collection<AuthData> auths = new ArrayList<>();

    @Override
    public AuthData createAuth(String username) {
        AuthData authData = new AuthData(UUID.randomUUID().toString(), username);
        auths.add(authData);
        return authData;
    }

    @Override
    public String getAuthToken(String username) {
        for (AuthData data : auths) {
            if (data.username().equals(username)) {
                return data.authToken();
            }
        }
        return null;
    }

    @Override
    public String getUsername(String authToken) {
        for (AuthData data : auths) {
            if (data.authToken().equals(authToken)) {
                return data.username();
            }
        }
        return null;
    }

    @Override
    public boolean containsToken(String authToken) {
        for (AuthData data : auths) {
            if (data.authToken().equals(authToken)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean deleteAuth(String authToken) {
        Iterator<AuthData> itr = auths.iterator();
        while (itr.hasNext()) {
            AuthData data = itr.next();
            if (data.authToken().equals(authToken)) {
                itr.remove();
                return true;
            }
        }
        return false;
    }

    @Override
    public void clear() {
        this.auths = new ArrayList<>();
    }
}
