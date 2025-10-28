import dataaccess.DatabaseManager;
import server.Server;

public final class Main {
    public static void main(String[] args) {
        final var server = new Server();

        try {
            DatabaseManager.createDatabase();
            DatabaseManager.runMigrations();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
        server.run(7070);
    }
}
