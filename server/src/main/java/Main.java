import io.javalin.Javalin;
import io.javalin.websocket.WsHandlerType;

import java.util.Set;

import handlers.*;

public final class Main {
    public static void main(String[] args) {
        final Set<Handler> handlers = Set.of(
            new ClearDBHandler(),
            new CreateGameHandler(),
            new JoinGameHandler(),
            new ListGamesHandler(),
            new LoginHandler(),
            new LogoutHandler(),
            new RegisterHandler()
        );

        final var app = Javalin.create();
        
        for (final var handler : handlers){
            app.addHttpHandler(handler.getHttpMethod(), handler.getPath(), handler::execute);
        }

        final var wsHandler = new PlayGameWsHandler();
        app.addWsHandler(WsHandlerType.WEBSOCKET, PlayGameWsHandler.PATH, wsHandler::handler);
        
        app.start(7070);
    }
}