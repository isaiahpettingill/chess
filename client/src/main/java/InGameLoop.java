import java.io.Console;

import client.ServerFacade;
import jakarta.websocket.Session;

public class InGameLoop {
    private static final Console CONSOLE = System.console();

    private ServerFacade backend;

    public InGameLoop(ServerFacade butt) {
        backend = butt;
    }

    public void move(Session connection) {

    }

    public void resign(Session connection) {

    }

    public void previewMove(Session connection) {

    }

    public void leave(Session connection) {

    }

    public void observe() {
        try {
            final var connection = backend.connectToWebSocket();
            var shouldQuit = false;
            loop: do {
                CONSOLE.printf("[l] leave\t[p]: preview move\n");
                final var action = CONSOLE.readLine("ACTION: ");
                switch (action.toLowerCase()) {
                    case "l":
                        leave(connection);
                        break loop;
                    case "p":
                        previewMove(connection);
                        break;
                    default:
                        CONSOLE.printf("Invalid Action\n");
                        continue loop;
                }
            } while (!shouldQuit);
            connection.close();
        } catch (Exception ex) {
            CONSOLE.printf("Something went terribly wrong!\n");
        }
    }

    public void play() {
        try {
            final var connection = backend.connectToWebSocket();
            var shouldQuit = false;
            loop: do {
                CONSOLE.printf("[m]: move\t[r]: resign\t[l] leave\t[p]: preview move\n");
                final var action = CONSOLE.readLine("ACTION: ");
                switch (action.toLowerCase()) {
                    case "m":
                        move(connection);
                        break;
                    case "r":
                        resign(connection);
                        break loop;
                    case "l":
                        leave(connection);
                        break loop;
                    case "p":
                        previewMove(connection);
                        break;
                    default:
                        CONSOLE.printf("Invalid Action\n");
                        continue loop;
                }
            } while (!shouldQuit);
            connection.close();
        } catch (Exception ex) {
            CONSOLE.printf("Something went terribly wrong!\n");
        }
    }
}
