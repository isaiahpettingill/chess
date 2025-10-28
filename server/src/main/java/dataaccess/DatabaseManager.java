package dataaccess;

import java.sql.*;
import java.util.Properties;

import database.MigrationRunner;

public final class DatabaseManager {
    private static String databaseName;
    private static String dbUsername;
    private static String dbPassword;
    private static String connectionUrl;

    /*
     * Load the database information for the db.properties file.
     */
    static {
        loadPropertiesFromResources();
    }

    /**
     * Creates the database if it does not already exist.
     */
    public static void createDatabase() throws DataAccessException {
        var statement = "CREATE DATABASE IF NOT EXISTS " + databaseName;
        try (var conn = DriverManager.getConnection(connectionUrl, dbUsername, dbPassword);
                var preparedStatement = conn.prepareStatement(statement)) {
            preparedStatement.executeUpdate();
        } catch (SQLException ex) {
            throw new DataAccessException("failed to create database", ex);
        }
    }

    public static void testConnection() throws DataAccessException, SQLException {
        try (final var connection = getConnection()) {
            final var props = getPropertiesFromResources();
            final var thedatabaseName = props.getProperty("db.name");
            final var thedbUsername = props.getProperty("db.user");
            final var thedbPassword = props.getProperty("db.password");
            if (!thedatabaseName.equals(databaseName) || !thedbUsername.equals(dbUsername) || !thedbPassword.equals(dbPassword)){
                throw new RuntimeException("Config does not match loaded values");
            }
        } catch (Exception ex) {
            throw ex;
        }
    }

    public static void clearDb() throws DataAccessException, SQLException {
        try (final var conn = getConnection()) {
            final var statement = conn.prepareStatement("truncate table users");
            statement.execute();
            final var statement2 = conn.prepareStatement("truncate table games");
            statement2.execute();
            final var statement3 = conn.prepareStatement("truncate table authTokens");
            statement3.execute();
        }
    }

    /**
     * Create a connection to the database and sets the catalog based upon the
     * properties specified in db.properties. Connections to the database should
     * be short-lived, and you must close the connection when you are done with it.
     * The easiest way to do that is with a try-with-resource block.
     * <br/>
     * <code>
     * try (var conn = DatabaseManager.getConnection()) {
     * // execute SQL statements.
     * }
     * </code>
     */
    public static Connection getConnection() throws DataAccessException {
        try {
            // do not wrap the following line with a try-with-resources
            var conn = DriverManager.getConnection(connectionUrl, dbUsername, dbPassword);
            conn.setCatalog(databaseName);
            return conn;
        } catch (SQLException ex) {
            throw new DataAccessException("failed to get connection", ex);
        }
    }

    public static void runMigrations() throws SQLException, DataAccessException {
        try (var conn = getConnection()) {
            MigrationRunner.migrate(conn);
        }
    }

    private static Properties getPropertiesFromResources(){
        try (var propStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("db.properties")) {
            if (propStream == null) {
                throw new Exception("Unable to load db.properties");
            }
            Properties props = new Properties();
            props.load(propStream);
            return props;
        } catch (Exception ex) {
            throw new RuntimeException("unable to process db.properties", ex);
        }
    }

    private static void loadPropertiesFromResources() {
        final var props = getPropertiesFromResources();
        loadProperties(props);
    }

    private static void loadProperties(Properties props) {
        databaseName = props.getProperty("db.name");
        dbUsername = props.getProperty("db.user");
        dbPassword = props.getProperty("db.password");

        var host = props.getProperty("db.host");
        var port = Integer.parseInt(props.getProperty("db.port"));
        connectionUrl = String.format("jdbc:mysql://%s:%d", host, port);
    }
}
