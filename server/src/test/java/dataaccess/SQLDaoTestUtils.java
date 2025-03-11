package dataaccess;

import static dataaccess.DatabaseManager.getConnection;

public class SQLDaoTestUtils {
    public static void cleanup() {
        try (var conn = getConnection()) {
            var statement = """
                    DELETE FROM `chess`.`users` WHERE email = 'testEmail'
                    """;
            try (var preparedStatement = conn.prepareStatement(statement)) {
                preparedStatement.executeUpdate();
            }
        } catch (Exception ex) {
            throw new AssertionError(
                    "EXCEPTION DURING CLEANUP: " + ex.getMessage()
            );
        }
    }
}
