package handlers;

import io.javalin.http.Context;
import io.javalin.http.HandlerType;

public class JoinGameHandler extends AuthorizedHandler implements Handler {

    @Override
    public void execute(Context context) {
        if (!authorize(context)) return;

        context.status(200);
        context.json(new Object());
    }

    @Override
    public HandlerType getHttpMethod() {
        return HandlerType.PUT;
    }

    @Override
    public String getPath() {
        return "/game";
    }

}
