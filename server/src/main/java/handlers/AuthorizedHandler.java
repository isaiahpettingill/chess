package handlers;

import java.util.Map;

import io.javalin.http.Context;

public abstract class AuthorizedHandler {
    protected boolean authorize(Context context) {
        var auth = context.header("Authoriztion");
        if (auth == null || auth == "") {
            context.status(401);
            context.json(Map.of("message", "Error: Unauthorized"));
            return false;
        }
        return true;
    }
}
