package dataaccess;

import java.sql.*;
import java.util.Properties;

public class DatabaseManager {
    private static final String DATABASE_NAME;
    private static final String USER;
    private static String password;
    private static final String CONNECTION_URL;

    static void setPassword(String password) {
        DatabaseManager.password = password;
    }

    static void revertPassword() throws DataAccessException {
        try (var propStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("db.properties")) {
            if (propStream == null) {
                throw new DataAccessException("Unable to load db.properties");
            }
            Properties props = new Properties();
            props.load(propStream);
            password = props.getProperty("db.password");
        } catch (Exception ex) {
            throw new DataAccessException(ex.getMessage());
        }
    }

    /*
     * Load the database information for the db.properties file.
     */
    static {
        try {
            try (var propStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("db.properties")) {
                if (propStream == null) {
                    throw new Exception("Unable to load db.properties");
                }
                Properties props = new Properties();
                props.load(propStream);
                DATABASE_NAME = props.getProperty("db.name");
                USER = props.getProperty("db.user");
                password = props.getProperty("db.password");

                var host = props.getProperty("db.host");
                var port = Integer.parseInt(props.getProperty("db.port"));
                CONNECTION_URL = String.format("jdbc:mysql://%s:%d", host, port);
            }
        } catch (Exception ex) {
            throw new RuntimeException("unable to process db.properties. " + ex.getMessage());
        }
    }

    private static final String[] CREATE_STATEMENTS = {
            "CREATE TABLE IF NOT EXISTS " + DATABASE_NAME +
            """
        .users (
          userID int PRIMARY KEY NOT NULL AUTO_INCREMENT,
          username varchar(256) NOT NULL,
          passwordHash varchar(512) NOT NULL,
          email varchar(256) NOT NULL
        )
        """,

            "CREATE TABLE IF NOT EXISTS " + DATABASE_NAME +
            """
        .sessions (
          sessionID int PRIMARY KEY NOT NULL AUTO_INCREMENT,
          userID int NOT NULL,
          authToken varchar(512) NOT NULL,
          INDEX (authToken),
          FOREIGN KEY (userID) REFERENCES users(userID)
        )
        """,

            "CREATE TABLE IF NOT EXISTS " + DATABASE_NAME +
            """
        .games (
          gameID int PRIMARY KEY NOT NULL AUTO_INCREMENT,
          whiteUserID int,
          blackUserID int,
          gameName varchar(256) NOT NULL,
          chessGameJson text NOT NULL,
          FOREIGN KEY (whiteUserID) REFERENCES users(userID),
          FOREIGN KEY (blackUserID) REFERENCES users(userID)
        )
        """
    };

    /**
     * Creates the database if it does not already exist.
     */
    static void configureDatabase() throws DataAccessException {
        try {
            var statement = "CREATE DATABASE IF NOT EXISTS " + DATABASE_NAME;
            var conn = DriverManager.getConnection(CONNECTION_URL, USER, password);
            try (var preparedStatement = conn.prepareStatement(statement)) {
                preparedStatement.executeUpdate();
            }
            for (var createStatement : CREATE_STATEMENTS) {
                try (var preparedStatement = conn.prepareStatement(createStatement)) {
                    preparedStatement.executeUpdate();
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    /**
     * Create a connection to the database and sets the catalog based upon the
     * properties specified in db.properties. Connections to the database should
     * be short-lived, and you must close the connection when you are done with it.
     * The easiest way to do that is with a try-with-resource block.
     * <br/>
     * <code>
     * try (var conn = DbInfo.getConnection(databaseName)) {
     * // execute SQL statements.
     * }
     * </code>
     */
    public static Connection getConnection() throws DataAccessException {
        try {
            var conn = DriverManager.getConnection(CONNECTION_URL, USER, password);
            conn.setCatalog(DATABASE_NAME);
            return conn;
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }
}
