package handlers;

import io.javalin.http.Context;
import io.javalin.http.HandlerType;

public class CreateGameHandler extends AuthorizedHandler implements Handler {

    @Override
    public void execute(Context context) {
        if (!authorize(context)) return;
        context.status(200);
        context.result("{}");
    }

    @Override
    public HandlerType getHttpMethod() {
        return HandlerType.POST;
    }

    @Override
    public String getPath() {
        return "/game";
    }

}
