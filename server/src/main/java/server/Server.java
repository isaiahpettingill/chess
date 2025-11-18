package server;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.google.gson.Gson;

import dataaccess.AuthRepository;
import dataaccess.DatabaseManager;
import dataaccess.GameRepository;
import dataaccess.UserRepository;
import dataaccess.inmemory.InMemoryAuthRepository;
import dataaccess.inmemory.InMemoryDatabase;
import dataaccess.inmemory.InMemoryGameRespository;
import dataaccess.inmemory.InMemoryUserRepository;
import handlers.*;
import io.javalin.*;
import io.javalin.websocket.WsContext;
import service.*;
import websocket.WebSocketHandler;

public class Server {

    private final Javalin javalinServer;

    public Server(boolean useInMemory) {
        javalinServer = Javalin.create(config -> config.staticFiles.add("/web"));

        final var db = useInMemory ? new InMemoryDatabase() : null;

        final var userRepository = useInMemory ? new InMemoryUserRepository(db) : new UserRepository();
        final var authRepository = useInMemory ? new InMemoryAuthRepository(db) : new AuthRepository();
        final var gameRepository = useInMemory ? new InMemoryGameRespository(db) : new GameRepository();

        final var userService = new UserService(userRepository);
        final var authService = new AuthService(authRepository, userRepository);
        final var gameService = new GameService(gameRepository);

        final Set<WsContext> sessions = ConcurrentHashMap.newKeySet();
        final var gson = new Gson();
        final var wsHandle = new WebSocketHandler(authService, gameService, msg -> {
            for (final var session : sessions) {
                session.send(gson.toJson(msg));
            }
        });

        final Set<Handler> handlers = Set.of(
                useInMemory ? new ClearDBHandler(db) : new ClearDBHandler(),
                new CreateGameHandler(authService, gameService),
                new GetGameHandler(authService, gameService),
                new JoinGameHandler(authService, gameService),
                new ListGamesHandler(authService, gameService),
                new LoginHandler(authService, userService),
                new LogoutHandler(authService),
                new RegisterHandler(userService, authService));

        for (final var handler : handlers) {
            javalinServer.addHttpHandler(handler.getHttpMethod(), handler.getPath(), handler::execute);
        }

        javalinServer.ws("/ws", cfg -> {
            cfg.onConnect(ctx -> {
                sessions.add(ctx);
                ctx.enableAutomaticPings();
            });

            cfg.onClose(ctx -> {
                sessions.remove(ctx);
            });

            cfg.onError(ctx -> {
                sessions.remove(ctx);
            });

            wsHandle.execute(cfg);
        });
    }

    public Server() {
        this(false);
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
