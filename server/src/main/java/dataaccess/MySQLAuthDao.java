package dataaccess;

import model.AuthData;

import java.sql.SQLException;
import java.util.UUID;

import static dataaccess.DatabaseManager.getConnection;


public class MySQLAuthDao implements AuthDao {
    public MySQLAuthDao() throws DataAccessException {
        DatabaseManager.configureDatabase();
    }

    private int getUserID(String username) throws DataAccessException {
        int userID;
        try {
            var conn = getConnection();
            var getUserIDStatement = """
                    SELECT (userID) FROM users WHERE username = ?
                    """;
            try (var preparedStatement = conn.prepareStatement(getUserIDStatement)) {
                preparedStatement.setString(1, username);
                var resultSet = preparedStatement.executeQuery();
                resultSet.next();
                userID = resultSet.getInt("userID");
            }
            conn.close();
        } catch (DataAccessException | SQLException ex) {
            throw new DataAccessException("Error: " + ex.getMessage());
        }
        return userID;
    }

    @Override
    public AuthData createAuth(String username) throws DataAccessException {
        String authToken = UUID.randomUUID().toString();
        try {
            var conn = getConnection();
            int userID = getUserID(username);
            var createAuthStatement = """
                    INSERT INTO sessions (userID, authToken) VALUES (?, ?)
                    """;
            try (var preparedStatement = conn.prepareStatement(createAuthStatement)) {
                preparedStatement.setInt(1, userID);
                preparedStatement.setString(2, authToken);
                preparedStatement.executeUpdate();
            }
            conn.close();
            return new AuthData(authToken, username);
        } catch (Exception ex) {
            throw new DataAccessException("Error: " + ex.getMessage());
        }
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
