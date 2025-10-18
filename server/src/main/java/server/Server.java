package server;

import java.util.Set;

import dataaccess.AuthRepository;
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

        var userRepository = new UserRepository();
        var userService = new UserService(userRepository);
        var authRepository = new AuthRepository();
        var authService = new AuthService(authRepository);

        final Set<Handler> handlers = Set.of(
            new ClearDBHandler(),
            new CreateGameHandler(authService),
            new JoinGameHandler(authService),
            new ListGamesHandler(authService),
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
