package dataaccess;

import model.UserData;

import java.util.HashMap;
import java.util.Map;

public class MemoryUserDao implements UserDao {
    private Map<String, UserData> users;

    public MemoryUserDao() {
        users = new HashMap<>();
    }

    @Override
    public UserData getUser(String username) throws DataAccessException {
        return users.get(username);
    }

    @Override
    public void createUser(UserData userData) throws DataAccessException {
        users.put(userData.username(), userData);
    }

    @Override
    public void clear() throws DataAccessException {
        users = new HashMap<>();
    }
}
