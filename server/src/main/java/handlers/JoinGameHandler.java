package handlers;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import dto.JoinGamePayload;
import io.javalin.http.Context;
import io.javalin.http.HandlerType;
import services.AuthService;
import services.GameService;

public final class JoinGameHandler extends AuthorizedHandler implements Handler {
    private final GameService _gameService;

    public JoinGameHandler(AuthService authService, GameService gameService) {
        super(authService);
        _gameService = gameService;
    }

    @Override
    public void execute(Context context) {
        if (!authorize(context))
            return;
        try {
            final var gson = new Gson();
            final var body = gson.fromJson(context.body(), JoinGamePayload.class);

            if (!body.valid() || !_gameService.gameExists(body.gameID())) {
                context.status(400);
                context.result(HttpErrors.BAD_REQUEST);
                return;
            }

            final var user = _authService.getUserFromToken(authToken(context).get());

            if (!user.isPresent()) { // This should never happen
                context.status(500);
                context.result(HttpErrors.createErrorMessage("Corrupt application state. User does not exist"));
                return;
            }

            if (_gameService.isPositionAlreadyTaken(body, user.get().username())) {
                context.status(403);
                context.result(HttpErrors.ALREADY_TAKEN);
                return;
            }

            _gameService.joinGame(body, user.get().username());
            context.status(200);
            context.result("{}");
        } catch (JsonSyntaxException ex) {
            context.status(400);
            context.result(HttpErrors.BAD_REQUEST);
        } catch (Exception ex) {
            context.status(500);
            context.result(HttpErrors.createErrorMessage(ex.getMessage()));
        }
    }

    @Override
    public HandlerType getHttpMethod() {
        return HandlerType.PUT;
    }

    @Override
    public String getPath() {
        return "/game";
    }

}
