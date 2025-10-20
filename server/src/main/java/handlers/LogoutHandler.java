package handlers;

import io.javalin.http.Context;
import io.javalin.http.HandlerType;
import services.AuthService;

public final class LogoutHandler extends AuthorizedHandler implements Handler {
    public LogoutHandler(AuthService authService) {
        super(authService);
    }

    @Override
    public void execute(Context context) {
        if (!authorize(context)){
            return;
        }

        try {
            final var token = authToken(context).get();

            _authService.logout(token);

            context.status(200);
            context.result("{}");

        } catch (Exception ex) {
            context.status(500);
            context.result(HttpErrors.createErrorMessage(ex.getMessage()));
        }
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
