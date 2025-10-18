package handlers;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import dto.RegisterPayload;
import dto.RegisterResponse;
import io.javalin.http.Context;
import io.javalin.http.HandlerType;
import services.*;

public final class RegisterHandler implements Handler {
    private final UserService _userService;
    private final AuthService _authService;
    public RegisterHandler(UserService userService, AuthService authService){
        _userService = userService;
        _authService = authService;
    }

    @Override
    public void execute(Context context) {
        var gson = new Gson();
        try {
            var body = gson.fromJson(context.body(), RegisterPayload.class);

            if (body.email() == null || body.password() == null || body.username() == null){
                context.status(400);
                context.result(HttpErrors.BAD_REQUEST);
                return;
            }

            if (_userService.isAlreadyTaken(body.username())) {
                context.result(HttpErrors.ALREADY_TAKEN);
                context.status(403);
                return;
            }

            _userService.saveUser(body);
            var response = new RegisterResponse(body.username(), "bob");
            
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
