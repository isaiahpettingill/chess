package handlers;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import dto.LoginPayload;
import dto.LoginResponse;
import io.javalin.http.Context;
import io.javalin.http.HandlerType;
import services.AuthService;
import services.UserService;

public final class LoginHandler implements Handler {
    private final AuthService authService;
    private final UserService userService;

    public LoginHandler(AuthService authService, UserService userService) {
        this.authService = authService;
        this.userService = userService;
    }

    @Override
    public void execute(Context context) {
        try {
            final var gson = new Gson();
            final var body = gson.fromJson(context.body(), LoginPayload.class);

            if (!body.valid()) {
                context.status(400);
                context.result(HttpErrors.BAD_REQUEST);
                return;
            }

            if (this.userService.validLogin(body.username(), body.password())) {
                final var token = this.authService.generateToken();
                this.authService.saveToken(token, body.username());

                final var response = new LoginResponse(body.username(), token.toString());
                context.status(200);
                context.result(gson.toJson(response));
            } else {
                context.status(401);
                context.result(HttpErrors.UNAUTHORIZED);
            }

        } catch (JsonSyntaxException ex) {
            context.status(400);
            context.result(HttpErrors.BAD_REQUEST);
        } catch (Exception ex) {
            context.status(500);
            context.result(HttpErrors.createErrorMessage(ex.getMessage()));
        }
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
