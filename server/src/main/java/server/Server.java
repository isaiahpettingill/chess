package server;

import java.util.Set;

import dataaccess.InMemoryAuthRepository;
import dataaccess.InMemoryDatabase;
import dataaccess.InMemoryGameRespository;
import dataaccess.InMemoryUserRepository;
import handlers.*;
import io.javalin.*;
import io.javalin.websocket.WsHandlerType;
import service.*;

public class Server {

    private final Javalin javalinServer;

    public Server() {
        javalinServer = Javalin.create(config -> 
            config.staticFiles.add("/web"));

        final var db = new InMemoryDatabase();

        final var userRepository = new InMemoryUserRepository(db);
        final var authRepository = new InMemoryAuthRepository(db);
        final var gameRepository = new InMemoryGameRespository(db);

        final var userService = new UserService(userRepository);
        final var authService = new AuthService(authRepository, userRepository);
        final var gameService = new GameService(gameRepository);

        final Set<Handler> handlers = Set.of(
            new ClearDBHandler(db),
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
