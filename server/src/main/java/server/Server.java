package server;

import java.util.Set;

import dataaccess.AuthRepository;
import dataaccess.GameRepository;
import dataaccess.UserRepository;
import handlers.*;
import io.javalin.*;
import io.javalin.websocket.WsHandlerType;
import services.*;

public class Server {

    private final Javalin javalinServer;

    public Server() {
        javalinServer = Javalin.create(config -> 
            config.staticFiles.add("web"));

        final var userRepository = new UserRepository();
        final var authRepository = new AuthRepository();
        final var gameRepository = new GameRepository();

        final var userService = new UserService(userRepository);
        final var authService = new AuthService(authRepository, userRepository);
        final var gameService = new GameService(gameRepository);

        final Set<Handler> handlers = Set.of(
            new ClearDBHandler(),
            new CreateGameHandler(authService, gameService),
            new JoinGameHandler(authService, gameService),
            new ListGamesHandler(authService, gameService),
            new LoginHandler(authService, userService),
            new LogoutHandler(authService),
            new RegisterHandler(userService, authService)
        );
        

        for (final var handler : handlers){
            javalinServer.addHttpHandler(handler.getHttpMethod(), handler.getPath(), handler::execute);
        }

        final var wsHandler = new PlayGameWsHandler();
        javalinServer.addWsHandler(WsHandlerType.WEBSOCKET, PlayGameWsHandler.PATH, wsHandler::handler);
    }

    public int run(int desiredPort) {
        javalinServer.start(desiredPort);
        return javalinServer.port();
    }

    public void stop() {
        javalinServer.stop();
    }
}
