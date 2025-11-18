package websocket;

import java.sql.SQLException;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;

import chess.ChessMove;
import chess.ChessGame;
import chess.ChessGame.TeamColor;
import dataaccess.DataAccessException;
import io.javalin.websocket.WsConfig;
import io.javalin.websocket.WsContext;
import models.Game;
import models.User;
import service.AuthService;
import service.GameService;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;
import websocket.messages.ServerMessage.ErrorMessage;
import websocket.messages.ServerMessage.LoadGameMessage;
import websocket.messages.ServerMessage.NotificationMessage;

public class WebSocketHandler {
    private static final Gson GSON = new Gson();
    private final AuthService authService;
    private final GameService gameService;
    private final NotifyAll notifier;

    public interface NotifyAll {
        void broadcast(ServerMessage msg);
    }

    public WebSocketHandler(AuthService authService, GameService gameService, NotifyAll notifyAll) {
        this.authService = authService;
        this.gameService = gameService;
        this.notifier = notifyAll;
    }

    private record UserAndGame(User user, Game game, Optional<TeamColor> playerColor, boolean isObserver,
            ChessGame gameContents) {
    };

    private UserAndGame getUserAndGame(String authToken, int gameID)
            throws SQLException, DataAccessException, NoSuchElementException, JsonParseException, JsonSyntaxException {
        final var user = authService.getUserFromToken(UUID.fromString(authToken)).get();
        final var game = gameService.getGame(gameID).get();
        final var black = game.blackUsername();
        final var white = game.whiteUsername();
        boolean isObserver = false;
        Optional<TeamColor> teamColor;
        if (user.username().equals(white)) {
            teamColor = Optional.of(TeamColor.WHITE);
        } else if (user.username().equals(black)) {
            teamColor = Optional.of(TeamColor.BLACK);
        } else {
            teamColor = Optional.empty();
            isObserver = true;
        }

        final var theGame = GSON.fromJson(game.game(), ChessGame.class);

        return new UserAndGame(user, game, teamColor, isObserver, theGame);
    }

    private void sendNotification(String message) {
        notifier.broadcast(new NotificationMessage(message));
    }

    private void connect(String authToken, int gameID, WsContext ctx) {
        try {
            final var userAndGame = getUserAndGame(authToken, gameID);
            ctx.send(GSON.toJson(new LoadGameMessage(userAndGame.gameContents())));
            sendNotification(userAndGame.user().username() + " joined the game "
                    + (userAndGame.isObserver()
                            ? "as observer."
                            : (userAndGame.playerColor().get() == TeamColor.WHITE
                                    ? "as white."
                                    : "as black.")));
        } catch (NoSuchElementException ex) {
            errorOut("The provided auth token is invalid or the game does not exist.", ctx);
        } catch (Exception ex) {
            errorOut(ex.getMessage(), ctx);
        }
    }

    private void resign(String authToken, int gameID, WsContext ctx) {
        try {
            final var userAndGame = getUserAndGame(authToken, gameID);
            final var game = userAndGame.game();
            gameService.markFinished(game);
            notifier.broadcast(new NotificationMessage(userAndGame.user().username() + " resigned. Game is over."));
        } catch (NoSuchElementException ex) {
            errorOut("The provided auth token is invalid or the game does not exist.", ctx);
        } catch (Exception ex) {
            errorOut(ex.getMessage(), ctx);
        }
    }

    private void move(String authToken, int gameID, ChessMove move, WsContext ctx) {
        try {
            final var userAndGame = getUserAndGame(authToken, gameID);
            final var user = userAndGame.user();
            final var game = userAndGame.game();
        } catch (NoSuchElementException ex) {
            errorOut("The provided auth token is invalid or the game does not exist.", ctx);
        } catch (Exception ex) {
            errorOut(ex.getMessage(), ctx);
        }
    }

    private void leave(String authToken, int gameID, WsContext ctx) {
        try {
            final var userAndGame = getUserAndGame(authToken, gameID);
            notifier.broadcast(new NotificationMessage(userAndGame.user().username() + " left the game."));
            ctx.closeSession();
        } catch (NoSuchElementException ex) {
            errorOut("The provided auth token is invalid or the game does not exist.", ctx);
        } catch (Exception ex) {
            errorOut(ex.getMessage(), ctx);
        }
    }

    private void errorOut(String errorMessage, WsContext ctx) {
        ctx.send(GSON.toJson(new ErrorMessage("Error: " + errorMessage)));
    }

    private void onMessage(WsContext ctx, UserGameCommand message) {
        switch (message.getCommandType()) {
            case CONNECT:
                connect(message.getAuthToken(), message.getGameID(), ctx);
                break;
            case RESIGN:
                resign(message.getAuthToken(), message.getGameID(), ctx);
                break;
            case MAKE_MOVE:
                if (!message.hasMove()) {
                    errorOut("Must send chess move!", ctx);
                    break;
                }
                move(message.getAuthToken(), message.getGameID(), message.chessMove(), ctx);
                break;
            case LEAVE:
                leave(message.getAuthToken(), message.getGameID(), ctx);
                break;
            default:
                break;
        }
    }

    public void execute(WsConfig cfg) {
        cfg.onBinaryMessage(ctx -> {
            final var message = ctx.data();
            final var asString = new String(message);
            final var msg = GSON.fromJson(asString, UserGameCommand.class);
            onMessage(ctx, msg);
        });

        cfg.onMessage(ctx -> {
            final var msg = GSON.fromJson(ctx.message(), UserGameCommand.class);
            onMessage(ctx, msg);
        });
    }
}
