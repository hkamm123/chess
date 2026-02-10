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
        try (var conn = getConnection();
             var statement = conn.prepareStatement("SELECT * FROM sessions WHERE authToken = ?")) {
            statement.setString(1, authToken);
            var resultSet = statement.executeQuery();
            if (resultSet.next()) {
                String foundToken = resultSet.getString("authToken");
                String username = resultSet.getString("username");
                return new AuthData(foundToken, username);
            } else {
                return null;
            }
        } catch (SQLException ex) {
            throw new DataAccessException(ex.getMessage());
        }
    }

    @Override
    public void deleteAuth(String authToken) throws DataAccessException {
        try (var conn = getConnection();
             var statement = conn.prepareStatement("DELETE FROM sessions WHERE authToken = ?")) {
            statement.setString(1, authToken);
            statement.executeUpdate();
        } catch (SQLException ex) {
            throw new DataAccessException(ex.getMessage());
        }
    }

    @Override
    public void clear() throws DataAccessException {
        try (var conn = getConnection(); var statement = conn.prepareStatement("TRUNCATE TABLE sessions")) {
            statement.executeUpdate();
        } catch (SQLException ex) {
            throw new DataAccessException(ex.getMessage());
        }
    }
}
