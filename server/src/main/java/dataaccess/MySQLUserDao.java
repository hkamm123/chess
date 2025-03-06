package dataaccess;

import model.UserData;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.SQLException;

import static org.mindrot.jbcrypt.BCrypt.checkpw;

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
    public boolean isValidCredentials(String username, String plainTextPassword) throws DataAccessException {
        String hashedPassword;
        try {
            var statement = "SELECT passwordHash from users WHERE username = ?";
            var conn = DatabaseManager.getConnection();
            try (var preparedStatement = conn.prepareStatement(statement)) {
                preparedStatement.setString(1, username);
                var rs = preparedStatement.executeQuery();
                rs.next();
                hashedPassword = rs.getString("passwordHash");
            }
        } catch (SQLException ex) {
            throw new DataAccessException(ex.getMessage());
        }

        return BCrypt.checkpw(plainTextPassword, hashedPassword);
    }

    @Override
    public void clear() {

    }
}
