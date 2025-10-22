package handlers;

import java.util.stream.Collectors;

import com.google.gson.GsonBuilder;

import dto.ListGamesResponse;
import io.javalin.http.Context;
import io.javalin.http.HandlerType;
import service.AuthService;
import service.GameService;

public final class ListGamesHandler extends AuthorizedHandler implements Handler {
    private final GameService gameService;

    public ListGamesHandler(AuthService authService, GameService gameService) {
        super(authService);
        this.gameService = gameService;
    }

    @Override
    public void execute(Context context) {
        if (!authorize(context)) {
            return;
        }

        final var games = this.gameService.listGames();
        final var response = new ListGamesResponse(
                games.stream()
                        .map(x -> new ListGamesResponse.ListGamesGame(
                            x.id(),
                            x.whiteUsername(),
                            x.blackUsername(),
                            x.gameName()
                        ))
                        .collect(Collectors.toList()));
        final var gson = new GsonBuilder().serializeNulls().create();

        context.status(200);
        context.result(gson.toJson(response));
    }

    @Override
    public HandlerType getHttpMethod() {
        return HandlerType.GET;
    }

    @Override
    public String getPath() {
        return "/game";
    }

}
