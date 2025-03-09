package dataaccess;

import model.AuthData;

import java.sql.SQLException;
import java.util.UUID;

import static dataaccess.DatabaseManager.getConnection;


public class MySQLAuthDao implements AuthDao {
    public MySQLAuthDao() throws DataAccessException {
        DatabaseManager.configureDatabase();
    }

    private int getUserIDFromUsername(String username) throws DataAccessException {
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
            int userID = getUserIDFromUsername(username);
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

    private int getUserIDFromAuthToken(String authToken) throws DataAccessException {
        try (var conn = getConnection()) {
            String userIDStatement = """
                    SELECT (userID) FROM sessions WHERE authToken = ?
                    """;
            try (var preparedStatement = conn.prepareStatement(userIDStatement)) {
                preparedStatement.setString(1, authToken);
                var resultSet = preparedStatement.executeQuery();
                resultSet.next();
                return resultSet.getInt("userID");
            }
        } catch (SQLException | DataAccessException ex) {
            throw new DataAccessException(ex.getMessage());
        }
    }

    @Override
    public String getUsername(String authToken) throws DataAccessException {
        try (var conn = getConnection()) {
            // get the userID using the authToken
            int userID = getUserIDFromAuthToken(authToken);

            // get the username using the userID
            String usernameStatement = """
                    SELECT (username) FROM users WHERE userID = ?
                    """;
            try (var preparedStatement = conn.prepareStatement(usernameStatement)) {
                preparedStatement.setInt(1, userID);
                var usernameResultSet = preparedStatement.executeQuery();
                usernameResultSet.next();
                return usernameResultSet.getString("username");
            }
        } catch (SQLException | DataAccessException ex) {
            throw new DataAccessException("Error: " + ex.getMessage());
        }
    }

    @Override
    public boolean deleteAuth(String authToken) throws DataAccessException {
        if (!containsToken(authToken)) {
            return false;
        }
        try (var conn = getConnection()) {
            String deleteStatement = """
                    DELETE FROM sessions WHERE authToken = ?
                    """;
            try (var preparedStatement = conn.prepareStatement(deleteStatement)) {
                preparedStatement.setString(1, authToken);
                preparedStatement.executeUpdate();
            }
            return true;
        } catch (SQLException | DataAccessException ex) {
            throw new DataAccessException("Error: " + ex.getMessage());
        }
    }

    @Override
    public boolean containsToken(String authToken) throws DataAccessException {
        try (var conn = getConnection()) {
            String queryStatement = """
                    SELECT * FROM sessions WHERE authToken = ?
                    """;
            try (var preparedStatement = conn.prepareStatement(queryStatement)) {
                preparedStatement.setString(1, authToken);
                var resultSet = preparedStatement.executeQuery();
                if (resultSet.next()) {
                    return true;
                } else {
                    return false;
                }
            }
        } catch (DataAccessException | SQLException ex) {
            throw new DataAccessException("Error: " + ex.getMessage());
        }
    }

    @Override
    public void clear() {
        throw new RuntimeException("not implemented");
    }
}
