package handlers;

import io.javalin.http.Context;
import io.javalin.http.HandlerType;

public class RegisterHandler implements Handler {

    @Override
    public void execute(Context context) {
        
        context.status(200);
        context.json(new Object());
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
