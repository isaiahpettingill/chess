package handlers;

import io.javalin.http.Context;
import io.javalin.http.HandlerType;

public class LoginHandler implements Handler {

    @Override
    public void execute(Context context) {
        context.status(200);
        context.result("{}");
    }

    @Override
    public HandlerType getHttpMethod() {
        return HandlerType.POST;

    }

    @Override
    public String getPath() {
        return "/session";
    }

}
