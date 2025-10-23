package database;

import java.sql.*;

public abstract class Migration {
    public final boolean executeUp(Connection connection) throws SQLException {
        final var statement = connection.prepareStatement(up());
        return statement.execute();
    }

    public final boolean executeDown(Connection connection) throws SQLException {
        final var statement = connection.prepareStatement(down());
        return statement.execute();
    }

    public abstract String up();
    public abstract String down();
}
