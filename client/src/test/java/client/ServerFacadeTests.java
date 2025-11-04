package client;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.util.Random;
import java.util.UUID;

import org.junit.jupiter.api.*;

import dto.CreateGamePayload;
import dto.JoinGamePayload;
import dto.LoginPayload;
import dto.RegisterPayload;
import server.Server;

@TestMethodOrder(MethodOrderer.MethodName.class)
public class ServerFacadeTests {

    private static Server server;
    private static ServerFacade connector;

    @BeforeAll
    public static void init() {
        server = new Server(true);
        var port = server.run(9090);
        System.out.println("Started test HTTP server on " + port);
        connector = new ServerFacade("http://localhost:9090");
    }

    @AfterAll
    public static void stopServer() {
        server.stop();
        ServerFacade.close();
    }

    @Order(1)
    @Test()
    public void testClearDbDoesNotThrow() throws IOException, InterruptedException {
        assertEquals(connector.clearDb().status(), 200);
    }

    @Order(2)
    @Test()
    public void clearDbWorks() throws IOException, InterruptedException {
        connector.register(new RegisterPayload(
                "bob", "bob", "bob"));
        connector.clearDb();
        final var login = connector.login(new LoginPayload("bob", "bob"));
        assertEquals(login.status(), 401);
    }

    @Order(3)
    @Test()
    public void registerWorks() throws IOException, InterruptedException {
        connector.register(new RegisterPayload(
                "bob", "bob", "bob"));
        final var login = connector.login(new LoginPayload("bob", "bob"));
        assertEquals(login.status(), 200);

    }

    @Order(4)
    @Test()
    public void canHasToken() throws IOException, InterruptedException {
        connector.register(new RegisterPayload(
                "bob", "bob", "bob"));
        assertEquals(connector.listGames().status(), 200);
    }

    @Order(5)
    @Test()
    public void canHasGame() throws IOException, InterruptedException {
        connector.clearDb();
        connector.register(new RegisterPayload(
                "bob", "bob", "bob"));
        final var result1 = connector.createGame(new CreateGamePayload(
                "the game of destiny"));
        assertEquals(result1.status(), 200);
        final var result = connector.listGames();
        assertEquals(result.status(), 200);
        assertNotNull(result.body());
        final var theGameIsThere = result.body()
                .games()
                .stream()
                .anyMatch(x -> x.gameName().equals("the game of destiny"));
        assertTrue(theGameIsThere);
    }

    @Order(6)
    @Test()
    public void cantHasGame() throws IOException, InterruptedException {
        connector.clearDb();
        final var result1 = connector.createGame(new CreateGamePayload(
                "the game of destiny"));
        assertEquals(result1.status(), 401);
        final var result = connector.listGames();
        assertEquals(result.status(), 401);
        assertNull(result.body());
    }

    @Order(7)
    @Test()
    public void joinGameWorks() throws IOException, InterruptedException {
        connector.clearDb();
        connector.register(new RegisterPayload(
                "bob", "bob", "bob"));
        final var game = connector.createGame(new CreateGamePayload(
                "your mom"));
        assertNotNull(game.body().gameID());
        final var id = game.body().gameID();
        final var result = connector.joinGame(new JoinGamePayload("WHITE", id));
        assertEquals(result.status(), 200);
        final var games = connector.listGames();
        assertTrue(games.body().games().stream().anyMatch(x -> "bob".equals(x.whiteUsername())));
    }

    @Order(8)
    @Test()
    public void joinGameForbidsJunk() throws IOException, InterruptedException {
        connector.clearDb();
        connector.register(new RegisterPayload(
                "bob", "bob", "bob"));
        final var game = connector.createGame(new CreateGamePayload(
                "your mom"));
        final var id = game.body().gameID();
        connector.joinGame(new JoinGamePayload("WHITE", id));
        connector.register(new RegisterPayload(
                "clide", "clide", "clide"));
        final var result2 = connector.joinGame(new JoinGamePayload("WHITE", id));
        assertEquals(result2.status(), 403);
    }

    @Order(9)
    @Test()
    public void listGameListsGames() throws IOException, InterruptedException {
        connector.clearDb();
        connector.register(new RegisterPayload(
                "bob", "bob", "bob"));
        final var count = (new Random(150)).nextInt(100, 700);
        for (int i = 0; i < count; i++) {
            connector.createGame(new CreateGamePayload(
                    UUID.randomUUID().toString()));
            Thread.sleep(10);
        }
        final var games = connector.listGames();
        assertEquals(games.status(), 200);
        assertEquals(games.body().games().size(), count);
    }

    @Order(10)
    @Test()
    public void logoutWorks() throws IOException, InterruptedException {
        connector.clearDb();
        connector.register(new RegisterPayload(
                "bob", "bob", "bob"));
        connector.logout();
        final var result = connector.listGames();
        assertEquals(result.status(), 401);
    }

    @Order(11)
    @Test()
    public void registerThrowsAlreadyTaken() throws IOException, InterruptedException {
        connector.clearDb();
        connector.register(new RegisterPayload("james", "dewey", "asdf"));
        final var result2 = connector.register(new RegisterPayload("james", "asdfrt", "2323"));
        assertEquals(result2.status(), 403);
    }

    @Order(12)
    @Test()
    public void badRequestForBadUserInformation() throws IOException, InterruptedException {
        connector.clearDb();
        final var result = connector.register(new RegisterPayload(null, null, null));
        assertEquals(result.status(), 400);
    }

    @Order(13)
    @Test()
    public void badRequestForBadGame() throws IOException, InterruptedException {
        connector.clearDb();
        connector.register(new RegisterPayload("james", "dewey", "asdf"));
        final var result = connector.createGame(new CreateGamePayload(null));
        assertEquals(result.status(), 400);
    }

    @Order(14)
    @Test()
    public void badRequestForBadJoin() throws IOException, InterruptedException {
        connector.clearDb();
        connector.register(new RegisterPayload("james", "dewey", "asdf"));
        final var game = connector.createGame(new CreateGamePayload(
                "your mom"));
        final var id = game.body().gameID();        
        
        final var result = connector.joinGame(null);
        assertEquals(result.status(), 400);

        final var result2 = connector.joinGame(new JoinGamePayload(null, null));
        assertEquals(result2.status(), 400);

        final var result3 = connector.joinGame(new JoinGamePayload("GREEN", id));
        assertEquals(result3.status(), 400);
    }


}
