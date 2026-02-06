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

    }

    @Override
    public void clear() throws DataAccessException {

    }
}
