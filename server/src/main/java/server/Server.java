package server;

import java.util.Set;

import handlers.ClearDBHandler;
import handlers.CreateGameHandler;
import handlers.JoinGameHandler;
import handlers.ListGamesHandler;
import handlers.LoginHandler;
import handlers.LogoutHandler;
import handlers.PlayGameWsHandler;
import handlers.RegisterHandler;
import io.javalin.*;
import handlers.*;
import io.javalin.websocket.WsHandlerType;

public class Server {

    private final Javalin javalin;

    public Server() {
        javalin = Javalin.create(config -> config.staticFiles.add("web"));

        final Set<Handler> handlers = Set.of(
            new ClearDBHandler(),
            new CreateGameHandler(),
            new JoinGameHandler(),
            new ListGamesHandler(),
            new LoginHandler(),
            new LogoutHandler(),
            new RegisterHandler()
        );
        
        for (final var handler : handlers){
            javalin.addHttpHandler(handler.getHttpMethod(), handler.getPath(), handler::execute);
        }

        final var wsHandler = new PlayGameWsHandler();
        javalin.addWsHandler(WsHandlerType.WEBSOCKET, PlayGameWsHandler.PATH, wsHandler::handler);
    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }
}
