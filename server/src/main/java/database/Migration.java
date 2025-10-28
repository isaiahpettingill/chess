package database;

import java.sql.*;

public abstract class Migration {
    public final void executeUp(Connection connection) throws SQLException {
        final var statements = up().trim().split(";");
        for (final var theStatement : statements) {
            try {
                final var statement = connection.prepareStatement(theStatement);
                statement.execute();
            } catch (Exception ex) {
                System.err.println(ex.getMessage());
            }
        }
    }

    public final void executeDown(Connection connection) throws SQLException {
        final var statements = down().trim().split(";");
        for (final var theStatement : statements) {
            try {
                final var statement = connection.prepareStatement(theStatement);
                statement.execute();
            } catch (Exception ex) {
                System.err.println(ex.getMessage());
            }
        }
    }

    public abstract String up();

    public abstract String down();
}
