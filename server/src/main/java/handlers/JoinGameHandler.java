package handlers;

import io.javalin.http.Context;
import io.javalin.http.HandlerType;
import services.AuthService;

public final class JoinGameHandler extends AuthorizedHandler implements Handler {

    public JoinGameHandler(AuthService authService) {
        super(authService);
        //TODO Auto-generated constructor stub
    }

    @Override
    public void execute(Context context) {
        if (!authorize(context)) return;

        context.status(200);
        context.result("{}");
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
