package dataaccess;

import model.UserData;

import java.sql.SQLException;

import static dataaccess.DatabaseManager.*;

public class MySQLUserDao implements UserDao {
    public MySQLUserDao() throws DataAccessException {
       createTables();
    }
    @Override
    public UserData getUser(String username) throws DataAccessException {
        try (
                var conn = getConnection();
                var statement = conn.prepareStatement("SELECT * FROM users WHERE username = ?")) {
            statement.setString(1, username);
            var resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return new UserData(
                        resultSet.getString("username"),
                        resultSet.getString("password"),
                        resultSet.getString("email")
                );
            } else {
                return null;
            }
        } catch (SQLException ex) {
            throw new DataAccessException(ex.getMessage());
        }
    }

    @Override
    public void createUser(UserData userData) throws DataAccessException {
        if (getUser(userData.username()) != null) {
            throw new DataAccessException("Username already exists");
        }
        try (
                var conn = getConnection();
                var statement = conn.prepareStatement("INSERT INTO users VALUES (?, ?, ?)")) {
            statement.setString(1, userData.username());
            statement.setString(1, userData.password());
            statement.setString(1, userData.email());
            statement.executeUpdate();
        } catch (SQLException ex) {
            throw new DataAccessException(ex.getMessage());
        }
    }

    @Override
    public void clear() throws DataAccessException {

    }
}
