package handlers;

import com.google.gson.GsonBuilder;

import dataaccess.DatabaseManager;
import io.javalin.http.Context;
import io.javalin.http.HandlerType;
import models.Game;
import service.AuthService;
import service.GameService;
import util.GameIdEncoder;

public final class GetGameHandler extends AuthorizedHandler implements Handler {
    private final GameService gameService;

    public GetGameHandler(AuthService authService, GameService gameService) {
        super(authService);
        this.gameService = gameService;
    }

    @Override
    public void execute(Context context) {
        if (!authorize(context)) {
            return;
        }

        try {
            DatabaseManager.testConnection();
            final var rawId = context.queryParam("gameId");
            final var parsedId = GameIdEncoder.decode(rawId);
            final var game = this.gameService.getGame(parsedId);
            final var response = game;
            if (game == null){
                context.status(404);
                context.result("{}");
                return;
            }
            final var gson = new GsonBuilder().serializeNulls().create();

            context.status(200);
            context.result(gson.toJson(response.get()));
        }
        catch (NumberFormatException ex){
            context.status(400); 
            context.result(HttpErrors.createErrorMessage("gameId must be a valid integer"));
        }
        catch (Exception ex) {
            context.status(500);
            context.result(HttpErrors.createErrorMessage(ex.getMessage()));
        }
    }

    @Override
    public HandlerType getHttpMethod() {
        return HandlerType.GET;
    }

    @Override
    public String getPath() {
        return "/single-game";
    }

}
