package handlers;

import dataaccess.DatabaseManager;
import io.javalin.http.Context;
import io.javalin.http.HandlerType;

public final class ClearDBHandler implements Handler {
    public ClearDBHandler() {
    }

    @Override
    public void execute(Context context) {
        try {
            DatabaseManager.clearDb();
        } catch (Exception ex) {
            context.status(500);
            context.result(HttpErrors.createErrorMessage(ex.getMessage()));
            return;
        }

        context.status(200);
        context.result("{}");
    }

    @Override
    public HandlerType getHttpMethod() {
        return HandlerType.DELETE;
    }

    @Override
    public String getPath() {
        return "/db";
    }
}
