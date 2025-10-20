package handlers;

import java.util.stream.Collectors;

import com.google.gson.Gson;

import dto.ListGamesResponse;
import io.javalin.http.Context;
import io.javalin.http.HandlerType;
import services.AuthService;
import services.GameService;

public final class ListGamesHandler extends AuthorizedHandler implements Handler {
    private final GameService _gameService;

    public ListGamesHandler(AuthService authService, GameService gameService) {
        super(authService);
        _gameService = gameService;
    }

    @Override
    public void execute(Context context) {
        if (!authorize(context))
            return;

        final var games = _gameService.listGames();
        final var response = new ListGamesResponse(
                games.stream()
                        .map(x -> new ListGamesResponse.ListGamesGame(
                            x.id(),
                            x.whiteUsername(),
                            x.blackUsername(),
                            x.gameName()
                        ))
                        .collect(Collectors.toSet()));
        final var gson = new Gson();

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
