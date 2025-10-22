package handlers;

import dataaccess.InMemoryDatabase;
import io.javalin.http.Context;
import io.javalin.http.HandlerType;

public final class ClearDBHandler implements Handler {
    private InMemoryDatabase database;
    public ClearDBHandler(InMemoryDatabase db){
        database = db;
    }

    @Override
    public void execute(Context context) {
        database.clearDb();

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
