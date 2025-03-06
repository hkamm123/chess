package dataaccess;

import model.AuthData;

import java.sql.SQLException;


public class MySQLAuthDao implements AuthDao {
    public MySQLAuthDao() throws DataAccessException {
        DatabaseManager.configureDatabase();
    }

    @Override
    public AuthData createAuth(String username) {
        throw new RuntimeException("not implemented");
    }

    @Override
    public String getAuthToken(String username) {
        throw new RuntimeException("not implemented");
    }

    @Override
    public String getUsername(String authToken) {
        throw new RuntimeException("not implemented");
    }

    @Override
    public boolean deleteAuth(String authToken) {
        throw new RuntimeException("not implemented");
    }

    @Override
    public boolean containsToken(String authToken) {
        throw new RuntimeException("not implemented");
    }

    @Override
    public void clear() {
        throw new RuntimeException("not implemented");
    }
}
