package dataaccess;

import model.UserData;

public class MySQLUserDao implements UserDao {
    public MySQLUserDao() throws DataAccessException {
        DatabaseManager.configureDatabase();
    }

    @Override
    public UserData getUser(String username) {
        return null;
    }

    @Override
    public void createUser(UserData userData) throws DataAccessException {

    }

    @Override
    public boolean isValidCredentials(String username, String password) {
        return false;
    }

    @Override
    public void clear() {

    }
}
