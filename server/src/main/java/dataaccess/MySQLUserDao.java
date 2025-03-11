package dataaccess;

import model.UserData;

import java.sql.SQLException;

import static dataaccess.DatabaseManager.getConnection;
import static org.mindrot.jbcrypt.BCrypt.checkpw;

public class MySQLUserDao implements UserDao {
    public MySQLUserDao() throws DataAccessException {
        DatabaseManager.configureDatabase();
    }

    @Override
    public UserData getUser(String username) throws DataAccessException {
        UserData output = null;

        try (var conn = getConnection()) {
            String statement = """
                    SELECT username, passwordHash, email FROM `chess`.`users`
                    WHERE username = ?
                    """;

            try (var preparedStatement = conn.prepareStatement(statement)) {
                preparedStatement.setString(1, username);
                var resultSet = preparedStatement.executeQuery();
                while (resultSet.next()) {
                    output = new UserData(
                            resultSet.getString("username"),
                            resultSet.getString("passwordHash"),
                            resultSet.getString("email")
                    );
                }
            }
        } catch (SQLException ex) {
            throw new DataAccessException("Error: " + ex.getMessage());
        }

        return output; // returns null if user does not exist
    }

    @Override
    public void createUser(UserData userData) throws DataAccessException {
        try (var conn = getConnection()) {
            String statement = """
                    INSERT INTO `chess`.`users` (username, passwordHash, email)
                    VALUES (?, ?, ?)
                    """;
            try (var preparedStatement = conn.prepareStatement(statement)) {
                preparedStatement.setString(1, userData.username());
                preparedStatement.setString(2, userData.password()); // service should've hashed it already
                preparedStatement.setString(3, userData.email());
                preparedStatement.executeUpdate();
            }
        } catch (SQLException ex) {
            throw new DataAccessException("Error: " + ex.getMessage());
        }
    }

    @Override
    public boolean isValidCredentials(String username, String plainTextPassword) throws DataAccessException {
        String hashedPassword;
        try (var conn = getConnection()) {
            var statement = "SELECT passwordHash from users WHERE username = ?";
            try (var preparedStatement = conn.prepareStatement(statement)) {
                preparedStatement.setString(1, username);
                var rs = preparedStatement.executeQuery();
                if (rs.next()) {
                    hashedPassword = rs.getString("passwordHash");
                } else {
                    return false;
                }
            }
        } catch (SQLException ex) {
            throw new DataAccessException(ex.getMessage());
        }

        return checkpw(plainTextPassword, hashedPassword);
    }

    @Override
    public void clear() throws DataAccessException {
        try (var conn = getConnection()) {
            var statement = """
                    DELETE FROM users
                    """;
            try (var preparedStatement = conn.prepareStatement(statement)) {
                preparedStatement.execute();
            }
        } catch (Exception ex) {
            throw new DataAccessException("Error: " + ex.getMessage());
        }
    }
}
