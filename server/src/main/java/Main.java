import server.Server;

public final class Main {
    public static void main(String[] args) {
        final var server = new Server();
        server.run(7070);
    }
}
