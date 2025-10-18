package handlers;

import io.javalin.http.Context;
import io.javalin.http.HandlerType;
import services.AuthService;

public final class ListGamesHandler extends AuthorizedHandler implements Handler {

    public ListGamesHandler(AuthService authService) {
        super(authService);
        // TODO Auto-generated constructor stub
    }

    @Override
    public void execute(Context context) {
        if (!authorize(context))
            return;

        context.status(200);
        context.result("{}");
    }

    @Override
    public HandlerType getHttpMethod() {
        return HandlerType.GET;
    }

    @Override
    public String getPath() {
        return "/game";
    }

}
