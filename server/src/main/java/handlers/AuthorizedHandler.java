package handlers;

import io.javalin.http.Context;

public abstract class AuthorizedHandler {
    protected boolean authorize(Context context) {
        var auth = context.header("Authoriztion");
        if (auth == null || auth == "") {
            context.status(401);
            context.result(HttpErrors.UNAUTHORIZED);
            return false;
        }
        return true;
    }
}
