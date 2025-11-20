import java.io.Console;
import java.io.IOException;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;
import chess.ChessGame.TeamColor;
import client.ServerFacade;
import client.WebSocketClient;
import websocket.commands.UserGameCommand;
import websocket.commands.UserGameCommand.CommandType;
import websocket.messages.ServerMessage;
import websocket.messages.ServerMessage.ErrorMessage;
import websocket.messages.ServerMessage.LoadGameMessage;
import websocket.messages.ServerMessage.NotificationMessage;

public class InGameLoop {
    private static final Console CONSOLE = System.console();
    private static final Gson GSON = new Gson();

    private final ServerFacade backend;
    private WebSocketClient webSocketClient;

    public InGameLoop(ServerFacade butt, int gameID, TeamColor color) {
        backend = butt;
        this.gameID = gameID;
        this.authToken = butt.getAuth();
        this.color = color;
    }

    private final TeamColor color;
    private String errorMessage;
    private String notificationMessage;
    private ChessGame game;
    private final String authToken;
    private final int gameID;

    private void move(WebSocketClient connection) throws IOException {
        final var p0 = CONSOLE.readLine("Starting position (chess notation, i.e., 'A1', 'B2'): ");
        final var p1 = CONSOLE.readLine("Ending position: ");
        final var p0Parsed = ChessPosition.fromChessNotation(p0);
        final var p1Parsed = ChessPosition.fromChessNotation(p1);
        if (p0Parsed.isEmpty() || p1Parsed.isEmpty()) {
            CONSOLE.printf("Invalid move! Use correct chess notation.");
            return;
        }

        final var chessMove = new ChessMove(p0Parsed.get(), p1Parsed.get());
        final var validMoves = game.validMoves(p0Parsed.get());
        if (!validMoves.contains(chessMove)) {

        }
    }

    private void resign(WebSocketClient connection) throws IOException {
        connection.send(GSON.toJson(
                new UserGameCommand(CommandType.RESIGN, authToken, gameID)));
    }

    private void previewMove(WebSocketClient connection) throws IOException {

    }

    private void leave(WebSocketClient connection) throws IOException {
        connection.send(GSON.toJson(
                new UserGameCommand(CommandType.LEAVE, authToken, gameID)));
    }

    private void connect(WebSocketClient connection) throws IOException {
        connection.send(GSON.toJson(
                new UserGameCommand(CommandType.CONNECT, authToken, gameID)));
    }

    public void onMessage(String message) {
        try {
            final var contents = GSON.fromJson(message, ServerMessage.class);
            switch (contents.getServerMessageType()) {
                case ERROR:
                    final var err = GSON.fromJson(message, ErrorMessage.class);
                    errorMessage = err.errorMessage();
                    CONSOLE.printf(errorMessage + "\n");
                    break;
                case NOTIFICATION:
                    final var notification = GSON.fromJson(message, NotificationMessage.class);
                    notificationMessage = notification.message();
                    CONSOLE.printf(notificationMessage + "\n");
                    break;
                case LOAD_GAME:
                    final var theGame = GSON.fromJson(message, LoadGameMessage.class);
                    game = theGame.game();
                    break;
            }
        } catch (JsonSyntaxException ex) {
            CONSOLE.printf("ERROR: Server sent invalid message.");
        }
    }

    public void printData() {
        if (game != null) {
            CONSOLE.printf(game.toPrettyString(color == TeamColor.BLACK));
        }
    }

    public void observe() {
        try {
            webSocketClient = backend.webSocketClient(this::onMessage);
            connect(webSocketClient);

            var shouldQuit = false;

            loop: do {

                CONSOLE.printf("[l] leave\t[p]: preview move\t[f]: refresh board\n");
                final var action = CONSOLE.readLine("ACTION: ");
                switch (action.toLowerCase()) {
                    case "l":
                        leave(webSocketClient);
                        break loop;
                    case "p":
                        previewMove(webSocketClient);
                        break;
                    case "f":
                        continue loop;
                    default:
                        CONSOLE.printf("Invalid Action\n");
                        continue loop;
                }
            } while (!shouldQuit);
        } catch (Exception ex) {
            CONSOLE.printf("Something went terribly wrong!\n");
        }
    }

    public void play() {
        try {
            webSocketClient = backend.webSocketClient(this::onMessage);
            connect(webSocketClient);
            var shouldQuit = false;

            loop: do {
                CONSOLE.printf("[m]: move\t[r]: resign\t[l] leave\t[p]: preview move\t[f]: refresh board\n");
                final var action = CONSOLE.readLine("ACTION: ");
                switch (action.toLowerCase()) {
                    case "m":
                        move(webSocketClient);
                        break;
                    case "r":
                        resign(webSocketClient);
                        break loop;
                    case "l":
                        leave(webSocketClient);
                        break loop;
                    case "p":
                        previewMove(webSocketClient);
                        break;
                    case "f":
                        continue loop;
                    default:
                        CONSOLE.printf("Invalid Action\n");
                        continue loop;
                }
            } while (!shouldQuit);
        } catch (Exception ex) {
            CONSOLE.printf("Something went terribly wrong!\n");
        }
    }
}
