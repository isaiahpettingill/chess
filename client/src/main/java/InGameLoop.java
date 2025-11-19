import java.io.Console;
import java.io.IOException;
import java.util.UUID;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;
import chess.ChessGame.TeamColor;
import client.ServerFacade;
import jakarta.websocket.MessageHandler;
import jakarta.websocket.Session;
import websocket.commands.UserGameCommand;
import websocket.commands.UserGameCommand.CommandType;
import websocket.messages.ServerMessage;
import websocket.messages.ServerMessage.ErrorMessage;
import websocket.messages.ServerMessage.LoadGameMessage;
import websocket.messages.ServerMessage.NotificationMessage;

public class InGameLoop {
    private static final Console CONSOLE = System.console();
    private static final Gson GSON = new Gson();

    private ServerFacade backend;

    public InGameLoop(ServerFacade butt, int gameID, UUID authToken, TeamColor color) {
        backend = butt;
        this.gameID = gameID;
        this.authToken = authToken;
        this.color = color;
    }

    private final TeamColor color;
    private String errorMessage;
    private String notificationMessage;
    private ChessGame game;
    private final UUID authToken;
    private final int gameID;

    private void move(Session connection) throws IOException {
        final var p0 = CONSOLE.readLine("Starting position (chess notation, i.e., 'A1', 'B2'): ");
        final var p1 = CONSOLE.readLine("Ending position: ");
        final var p0Parsed = ChessPosition.fromChessNotation(p0);
        final var p1Parsed = ChessPosition.fromChessNotation(p1);
        if (p0Parsed.isEmpty() || p1Parsed.isEmpty()){
            CONSOLE.printf("Invalid move! Use correct chess notation.");
            return;
        }

        final var chessMove = new ChessMove(p0Parsed.get(), p1Parsed.get());
        final var validMoves = game.validMoves(p0Parsed.get());
        if (!validMoves.contains(chessMove)){
            
        }
    }

    private void resign(Session connection) throws IOException {
        connection.getBasicRemote().sendText(GSON.toJson(
            new UserGameCommand(CommandType.RESIGN, authToken.toString(), gameID)));
    }

    private void previewMove(Session connection) throws IOException {

    }

    private void leave(Session connection) throws IOException {
        connection.getBasicRemote().sendText(GSON.toJson(
                new UserGameCommand(CommandType.LEAVE, authToken.toString(), gameID)));
    }

    private void connect(Session connection) throws IOException {
        connection.getBasicRemote().sendText(GSON.toJson(
                new UserGameCommand(CommandType.CONNECT, authToken.toString(), gameID)));
    }

    private void setupHandlers(Session connection) {
        connection.addMessageHandler(new MessageHandler.Whole<String>() {
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
        });
    }

    public void printData() {
        if (game != null) {
            CONSOLE.printf(game.toPrettyString(color == TeamColor.BLACK));
        }
    }

    public void observe() {
        try {
            final var connection = backend.connectToWebSocket();
            connect(connection);

            var shouldQuit = false;
            setupHandlers(connection);

            loop: do {

                CONSOLE.printf("[l] leave\t[p]: preview move\t[f]: refresh board\n");
                final var action = CONSOLE.readLine("ACTION: ");
                switch (action.toLowerCase()) {
                    case "l":
                        leave(connection);
                        break loop;
                    case "p":
                        previewMove(connection);
                        break;
                    case "f":
                        continue loop;
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
            connect(connection);

            var shouldQuit = false;
            setupHandlers(connection);

            loop: do {
                CONSOLE.printf("[m]: move\t[r]: resign\t[l] leave\t[p]: preview move\t[f]: refresh board\n");
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
                    case "f":
                        continue loop;
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
