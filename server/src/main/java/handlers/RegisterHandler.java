package handlers;

import java.util.UUID;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import dto.RegisterPayload;
import dto.RegisterResponse;
import io.javalin.http.Context;
import io.javalin.http.HandlerType;
import service.*;

public final class RegisterHandler implements Handler {
    private final UserService userService;
    private final AuthService authService;
    public RegisterHandler(UserService userService, AuthService authService){
        this.userService = userService;
        this.authService = authService;
    }

    @Override
    public void execute(Context context) {
        final var gson = new Gson();
        try {
            final var body = gson.fromJson(context.body(), RegisterPayload.class);

            if (!body.valid()){
                context.status(400);
                context.result(HttpErrors.BAD_REQUEST);
                return;
            }

            if (this.userService.isAlreadyTaken(body.username())) {
                context.result(HttpErrors.ALREADY_TAKEN);
                context.status(403);
                return;
            }

            this.userService.saveUser(body);
            final var token = UUID.randomUUID();
            this.authService.saveToken(token, body.username());

            var response = new RegisterResponse(body.username(), token.toString());
            
            context.status(200);
            context.result(gson.toJson(response));
        }
        catch (JsonSyntaxException ex){
            context.status(400);
            context.result(HttpErrors.BAD_REQUEST);
        }
        catch (Exception ex){
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
        return "/user";
    }

}
