package dataaccess;

import model.AuthData;
import model.UserData;

public interface UserDao {
    UserData getUser(String username);

    void createUser(UserData userData);

    void createAuth(AuthData authData);
}
