package database;

import java.sql.*;
import java.util.List;

import database.migrations.*;

public final class MigrationRunner {
    private static final List<Migration> MIGRATIONS = List.of(
        new Migration0001()
    );

    public static final void migrate(Connection connection) throws SQLException {
        for (final var migration : MIGRATIONS){
            migration.executeUp(connection);
        }
    } 
}
