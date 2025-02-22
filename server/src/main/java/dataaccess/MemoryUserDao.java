package dataaccess;

import model.AuthData;
import model.UserData;

import java.util.ArrayList;
import java.util.Objects;

public class MemoryUserDao implements UserDao {
    private ArrayList<UserData> users = new ArrayList<UserData>();

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
    public boolean isValidCredentials(String username, String password) {
        boolean output = false;
        for (UserData user : users) {
            if (user.username().equals(username) && user.password().equals(password)) {
                output = true;
                break;
            }
        }
        return output;
    }

    @Override
    public void clear() {
        this.users = new ArrayList<>();
    }
}
