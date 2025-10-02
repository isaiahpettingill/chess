package handlers;

import io.javalin.http.Context;
import io.javalin.http.HandlerType;

public interface Handler {
    void execute(Context context);
    HandlerType getHttpMethod();
    String getPath();
}
