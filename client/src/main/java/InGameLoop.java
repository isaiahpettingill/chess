import static ui.EscapeSequences.*;

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
    private boolean isPlayer = false;

    private void move(WebSocketClient connection) throws IOException {
        final var p0 = CONSOLE.readLine("Starting position (chess notation, i.e., 'A1', 'B2'): ");
        final var p1 = CONSOLE.readLine("Ending position: ");
        final var p0Parsed = ChessPosition.fromChessNotation(p0);
        final var p1Parsed = ChessPosition.fromChessNotation(p1);
        if (p0Parsed.isEmpty() || p1Parsed.isEmpty()) {
            System.out.println("Invalid move! Use correct chess notation.");
            return;
        }

        final var chessMove = new ChessMove(p0Parsed.get(), p1Parsed.get());
        final var validMoves = game.validMoves(p0Parsed.get());
        if (!validMoves.contains(chessMove)) {
            System.out.println("Invalid move! That piece can't move there.");
            return;
        }

        connection.send(GSON.toJson(
                new UserGameCommand(authToken, gameID, chessMove)));
    }

    private void resign(WebSocketClient connection) throws IOException {
        connection.send(GSON.toJson(
                new UserGameCommand(CommandType.RESIGN, authToken, gameID)));
        System.out.println("You have resigned the game.\n");
    }

    private void previewMove(WebSocketClient connection) throws IOException {
        final var startPosition = CONSOLE.readLine("Starting position (chess notation, e.g., A2): ");
        final var pos0 = ChessPosition.fromChessNotation(startPosition);
        if (pos0.isEmpty()) {
            System.out.println("Invalid position! Use correct chess notation.");
            return;
        }
        final var piece = game.getBoard().getPiece(pos0.get());
        if (piece == null) {
            System.out.println("No piece at that position!");
            return;
        }
        if (isPlayer && piece.getTeamColor() != color) {
            System.out.println("That's not your piece!");
            return;
        }

        final Collection<ChessMove> validMoves = game.validMoves(pos0.get());
        if (validMoves.isEmpty()) {
            errorMessage = "No valid moves for that piece!";
        }

        System.out.println(game.toPrettyString(color == TeamColor.BLACK, pos0));
        notificationMessage = "Valid moves for that piece: " + validMoves.stream()
                .map(m -> m.getEndPosition().toString())
                .reduce((a, b) -> a + ", " + b)
                .orElse("");
        preview = true;
    }

    private void notificationPrint() {
        System.out.print(ERASE_SCREEN);
        printData();
        prompt();
    }

    private void prompt() {
        StringBuilder thePrompt = new StringBuilder();
        if (isPlayer) {
            thePrompt.append("[m]: move\t");
            thePrompt.append("[r]: resign\t");
        }
        thePrompt.append("[l] leave\t[p]: preview move\t[f]: refresh board\n");
        System.out.println(thePrompt.toString());

    }

    private boolean handleAction(String action, WebSocketClient webSocketClient) throws IOException {
        switch (action.toLowerCase()) {
            case "m":
                if (!isPlayer) {
                    System.out.println("Invalid Action\n");
                    return true;
                }
                move(webSocketClient);
                return false;
            case "r":
                if (!isPlayer) {
                    System.out.println("Invalid Action\n");
                    return true;
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
                System.out.println("Invalid Action\n");
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
                    notificationPrint();
                    break;
                case NOTIFICATION:
                    final var notification = GSON.fromJson(message, NotificationMessage.class);
                    notificationMessage = notification.message();
                    notificationPrint();
                    break;
                case LOAD_GAME:
                    final var theGame = GSON.fromJson(message, LoadGameMessage.class);
                    game = theGame.game();
                    notificationPrint();
                    break;
            }
        } catch (JsonSyntaxException ex) {
            System.out.println("ERROR: Server sent invalid message.");
        }
    }

    public void printData() {
        if (game != null) {
            if (!preview) {
                System.out.print(game.toPrettyString(color == TeamColor.BLACK));
            }
            System.out.printf("\nIt's %s's turn.\n", game.getTeamTurn());
            preview = false;
        }
        if (errorMessage != null) {
            System.out.printf(SET_TEXT_COLOR_RED + "ERROR: %s\n" + RESET_TEXT_COLOR, errorMessage);
        }
        if (notificationMessage != null) {
            System.out.println(notificationMessage + "\n");
        }
    }

    private void runGameLoop() {
        try {
            webSocketClient = backend.webSocketClient(this::onMessage);
            connect(webSocketClient);
            var shouldQuit = false;

            loop: do {
                printData();
                prompt();
                final var action = CONSOLE.readLine();
                if (handleAction(action, webSocketClient)) {
                    break loop;
                }

            } while (!shouldQuit);
        } catch (JsonSyntaxException ex) {
            System.out.println("ERROR: Server sent invalid message.\n");
        } catch (Exception ex) {
            System.out.println("Something went terribly wrong!\n");
        }
    }

    public void observe() {
        isPlayer = false;
        runGameLoop();
    }

    public void play() {
        isPlayer = true;
        runGameLoop();
    }
}
