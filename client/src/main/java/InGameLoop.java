import java.io.Console;

import client.ServerFacade;

public class InGameLoop {
    private static final Console CONSOLE = System.console();

    private ServerFacade backend;

    public InGameLoop(ServerFacade butt) {
        backend = butt;
    }

    public void loop() {
        try {
            final var connection = backend.connectToWebSocket();
            var shouldQuit = false;
            do {
                CONSOLE.printf("[m]: move\t[r]: resign\t[l] leave\t[p]: preview move\n");
                final var action = CONSOLE.readLine("ACTION: ");
                switch (action.toLowerCase()) {
                    case "m":
                        break;
                    case "r":
                        shouldQuit = true;
                        break;
                    case "l":
                        shouldQuit = true;
                        break;
                    case "p":
                        break;
                    default:
                        CONSOLE.printf("Invalid Action\n");
                        break;
                }
            } while (!shouldQuit);
            connection.close();
        } catch (Exception ex) {
            CONSOLE.printf("Something went terribly wrong!\n");
        }
    }
}
