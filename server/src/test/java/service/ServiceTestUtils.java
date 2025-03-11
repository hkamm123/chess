package service;

import dataaccess.DataAccessException;

import java.sql.SQLException;

import static dataaccess.DatabaseManager.getConnection;

public class ServiceTestUtils {
    public static void cleanupUsersAndSessions() {
        try (var conn = getConnection()) {
            int userID = 0;
            try (var preparedStatement = conn.prepareStatement(
                    "SELECT userID FROM users WHERE username = 'testUser'")) {
                var resultSet = preparedStatement.executeQuery();
                if (resultSet.next()) {
                    userID = resultSet.getInt("userID");
                }
            }
            try (var preparedStatement = conn.prepareStatement(
                    "DELETE FROM sessions WHERE userID = " + userID)) {
                preparedStatement.execute();
            }
            try (var preparedStatement = conn.prepareStatement(
                    "DELETE FROM users WHERE username = 'testUser'")) {
                preparedStatement.execute();
            }
        } catch (DataAccessException | SQLException ex) {
            throw new AssertionError(ex.getMessage());
        }
    }
}
