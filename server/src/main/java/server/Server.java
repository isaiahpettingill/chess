package server;

import java.util.Set;

import dataaccess.AuthRepository;
import dataaccess.DatabaseManager;
import dataaccess.GameRepository;
import dataaccess.UserRepository;
import handlers.*;
import io.javalin.*;
import service.*;

public class Server {

    private final Javalin javalinServer;

    public Server() {
        javalinServer = Javalin.create(config -> config.staticFiles.add("/web"));

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
                new RegisterHandler(userService, authService));

        for (final var handler : handlers) {
            javalinServer.addHttpHandler(handler.getHttpMethod(), handler.getPath(), handler::execute);
        }
    }

    public int run(int desiredPort) {
        try {
            DatabaseManager.createDatabase();
            DatabaseManager.runMigrations();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
        javalinServer.start(desiredPort);
        return javalinServer.port();
    }

    public void stop() {
        javalinServer.stop();
    }
}
