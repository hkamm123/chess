package dataaccess;

import model.AuthData;
import model.UserData;

import java.util.ArrayList;
import java.util.Objects;

public class MemoryUserDao implements UserDao{
    private ArrayList<UserData> users = new ArrayList<UserData>();
    private ArrayList<AuthData> auths = new ArrayList<AuthData>();
    @Override
    public UserData getUser(String username) {
        for (UserData user : users) {
            if (Objects.equals(user.username(), username)) {
                return user;
            }
        }
        return null;
    }

    @Override
    public void createUser(UserData userData) throws DataAccessException {
        if (getUser(userData.username()) == null) {
            users.add(userData);
        } else {
            throw new DataAccessException(USER_TAKEN_ERR_MSG);
        }
    }

    @Override
    public void createAuth(AuthData authData) {
        auths.add(authData);
    }
}
