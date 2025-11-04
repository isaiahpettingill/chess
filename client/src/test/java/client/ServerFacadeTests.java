package client;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import java.io.IOException;

import org.junit.jupiter.api.*;

import dto.RegisterPayload;
import server.Server;

@TestMethodOrder(MethodOrderer.MethodName.class)
public class ServerFacadeTests {

    private static Server server;
    private static BackendConnector connector;

    @BeforeAll
    public static void init() {
        server = new Server(true);
        var port = server.run(9090);
        System.out.println("Started test HTTP server on " + port);
        connector = new BackendConnector("http://localhost:9090");
    }

    @AfterAll
    public static void stopServer() {
        server.stop();
        BackendConnector.close();
    }

    @Order(1)
    public void testClearDbDoesNotThrow(){
        assertDoesNotThrow(connector::clearDb);
    }

    @Order(2)
    public void clearDbWorks() throws IOException, InterruptedException {
        connector.register(new RegisterPayload(
            "bob", "bob", "bob"
        ));
        connector.clearDb();
    }
}
