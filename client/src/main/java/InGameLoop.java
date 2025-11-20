import static ui.EscapeSequences.RESET_TEXT_COLOR;
import static ui.EscapeSequences.SET_TEXT_COLOR_RED;

import java.io.Console;
import java.io.IOException;
import java.util.Collection;

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
    private boolean preview = false;
    private boolean boardRendered = false;

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
            CONSOLE.printf("Invalid move! That piece can't move there.");
            return;
        }

        boardRendered = false;
        connection.send(GSON.toJson(
                new UserGameCommand(authToken, gameID, chessMove)));
    }

    private void resign(WebSocketClient connection) throws IOException {
        connection.send(GSON.toJson(
                new UserGameCommand(CommandType.RESIGN, authToken, gameID)));
        CONSOLE.printf("You have resigned the game.\n");
    }

    private void previewMove(WebSocketClient connection) throws IOException {
        final var startPosition = CONSOLE.readLine("Starting position (chess notation, e.g., A2): ");
        final var pos0 = ChessPosition.fromChessNotation(startPosition);
        if (pos0.isEmpty()) {
            CONSOLE.printf("Invalid position! Use correct chess notation.");
            return;
        }
        final var piece = game.getBoard().getPiece(pos0.get());
        if (piece == null) {
            CONSOLE.printf("No piece at that position!");
            return;
        }
        if (piece.getTeamColor() != color) {
            CONSOLE.printf("That's not your piece!");
            return;
        }

        final Collection<ChessMove> validMoves = game.validMoves(pos0.get());
        if (validMoves.isEmpty()) {
            errorMessage = "No valid moves for that piece!";
        }

        CONSOLE.printf(game.toPrettyString(color == TeamColor.BLACK, pos0));
        notificationMessage = "Valid moves for that piece: " + validMoves.stream()
                .map(m -> m.getEndPosition().toString())
                .reduce((a, b) -> a + ", " + b)
                .orElse("");
        preview = true;
    }

    private String buildPrompt(boolean isPlayer) {
        StringBuilder prompt = new StringBuilder();
        if (isPlayer) {
            prompt.append("[m]: move\t");
            prompt.append("[r]: resign\t");
        }
        prompt.append("[l] leave\t[p]: preview move\t[f]: refresh board\n");
        return prompt.toString();
    }

    private boolean handleAction(String action, WebSocketClient webSocketClient, boolean isPlayer) throws IOException {
        switch (action.toLowerCase()) {
            case "m":
                if (!isPlayer) {
                    CONSOLE.printf("Invalid Action\n");
                    return false;
                }
                move(webSocketClient);
                return false;
            case "r":
                if (!isPlayer) {
                    CONSOLE.printf("Invalid Action\n");
                    return false;
                }
                resign(webSocketClient);
                return true;
            case "l":
                leave(webSocketClient);
                return true;
            case "p":
                previewMove(webSocketClient);
                return false;
            case "f":
                return false;
            default:
                CONSOLE.printf("Invalid Action\n");
                return false;
        }
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
                    boardRendered = true;
                    break;
            }
        } catch (JsonSyntaxException ex) {
            CONSOLE.printf("ERROR: Server sent invalid message.");
        }
    }

    public void printData() {
        if (game != null) {
            if (!preview) {
                CONSOLE.printf(game.toPrettyString(color == TeamColor.BLACK));
            }
            CONSOLE.printf("\nIt's %s's turn.\n", game.getTeamTurn());
            preview = false;
        }
        if (errorMessage != null) {
            CONSOLE.printf(SET_TEXT_COLOR_RED + "ERROR: %s\n" + RESET_TEXT_COLOR, errorMessage);
            errorMessage = null;
        }
        if (notificationMessage != null) {
            CONSOLE.printf(notificationMessage + "\n");
            notificationMessage = null;
        }
    }

    private void runGameLoop(boolean isPlayer) {
        try {
            webSocketClient = backend.webSocketClient(this::onMessage);
            connect(webSocketClient);
            var shouldQuit = false;

            loop: do {
                printData();
                if (boardRendered) {
                    CONSOLE.printf(buildPrompt(isPlayer));
                    final var action = CONSOLE.readLine("ACTION: ");
                    if (handleAction(action, webSocketClient, isPlayer)) {
                        break loop;
                    }
                } else {
                    Thread.sleep(5);
                }
            } while (!shouldQuit);
        } catch (JsonSyntaxException ex) {
            CONSOLE.printf("ERROR: Server sent invalid message.");
        } catch (Exception ex) {
            CONSOLE.printf("Something went terribly wrong!\n");
        }
    }

    public void observe() {
        runGameLoop(false);
    }

    public void play() {
        runGameLoop(true);
    }
}
