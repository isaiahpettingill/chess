package handlers;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import dto.CreateGamePayload;
import dto.CreateGameResponse;
import io.javalin.http.Context;
import io.javalin.http.HandlerType;
import services.AuthService;
import services.GameService;

public final class CreateGameHandler extends AuthorizedHandler implements Handler {
    private final GameService _gameService;

    public CreateGameHandler(AuthService authService, GameService gameService) {
        super(authService);
        _gameService = gameService;
    }

    @Override
    public void execute(Context context) {
        if (!authorize(context))
            return;

        try {
            final var gson = new Gson();
            final var body = gson.fromJson(context.body(), CreateGamePayload.class);
            if (!body.valid()){
                context.status(400);
                context.result(HttpErrors.BAD_REQUEST);
                return;
            }

            var game = _gameService.createGame(body);
            
            if (game.id() == null){
                context.status(500);
                context.result(HttpErrors.createErrorMessage("Could not create game"));
            }
            var reponse = new CreateGameResponse(game.id());

            context.status(200);
            context.result(gson.toJson(reponse));
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
        return HandlerType.POST;
    }

    @Override
    public String getPath() {
        return "/game";
    }

}
