package handlers;

import io.javalin.http.Context;
import io.javalin.http.HandlerType;
import services.AuthService;

public final class LoginHandler implements Handler {
    private final AuthService _authService;
    public LoginHandler(AuthService authService){
        _authService = authService;
    }
    
    @Override
    public void execute(Context context) {
        context.status(200);
        context.result("{}");
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
