package dataaccess;

import model.AuthData;

import java.sql.SQLException;

import static dataaccess.DatabaseManager.*;

public class MySQLAuthDao implements AuthDao {
    public MySQLAuthDao() throws DataAccessException {
        createTables();
    }

    @Override
    public void createAuth(AuthData authData) throws DataAccessException {
        try (var conn = getConnection();
             var statement = conn.prepareStatement("INSERT INTO sessions VALUES (?, ?)")) {
            statement.setString(1, authData.authToken());
            statement.setString(2, authData.username());
            statement.execute();
        } catch (SQLException ex) {
            throw new DataAccessException(ex.getMessage());
        }
    }

    @Override
    public AuthData getAuth(String authToken) throws DataAccessException {
        return null;
    }

    @Override
    public void deleteAuth(String authToken) throws DataAccessException {

    }

    @Override
    public void clear() throws DataAccessException {

    }
}
