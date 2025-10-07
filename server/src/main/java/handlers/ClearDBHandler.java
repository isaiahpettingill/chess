package handlers;

import io.javalin.http.Context;
import io.javalin.http.HandlerType;

public class ClearDBHandler implements Handler {

    @Override
    public void execute(Context context) {
        context.status(418);
        context.html("I'm a teapot");
    }

    @Override
    public HandlerType getHttpMethod() {
        return HandlerType.DELETE;
    }

    @Override
    public String getPath() {
        return "/clear-db";
    }

}
