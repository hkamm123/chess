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
    public void createUser(UserData userData) {
        users.add(userData);
    }

    @Override
    public void createAuth(AuthData authData) {
        auths.add(authData);
    }
}
