package handlers;

import io.javalin.http.Context;
import io.javalin.http.HandlerType;
import services.AuthService;

public final class LogoutHandler extends AuthorizedHandler implements Handler {
    public LogoutHandler(AuthService authService){
        super(authService);
    }

    @Override
    public void execute(Context context) {
        if (!authorize(context)) return;

        _authService.logout(authToken(context).get());

        context.status(200);
        context.result("{}");
    }

    @Override
    public HandlerType getHttpMethod() {
        return HandlerType.DELETE;
    }

    @Override
    public String getPath() {
        return "/session";
    }

}
