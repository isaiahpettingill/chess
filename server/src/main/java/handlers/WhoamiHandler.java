package handlers;

import io.javalin.http.Context;
import io.javalin.http.HandlerType;
import service.AuthService;

public class WhoamiHandler extends AuthorizedHandler implements Handler {
    public WhoamiHandler(AuthService authService) {
        super(authService);
    }

    @Override
    public void execute(Context context) {
        try {
            if (!authorize(context)) {
                return;
            }
            final var user = authService.getUserFromToken(authToken(context).get());

            if (!user.isPresent()) { // This should never happen
                context.status(500);
                context.result(HttpErrors.createErrorMessage("Corrupt application state. User does not exist"));
                return;
            }

            context.status(200);
            context.result(user.get().username());
        } catch (Exception ex) {
            context.status(500);
            context.result(HttpErrors.createErrorMessage(ex.getMessage()));
        }
    }

    @Override
    public HandlerType getHttpMethod() {
        return HandlerType.GET;
    }

    @Override
    public String getPath() {
        return "/whoami";
    }

}
