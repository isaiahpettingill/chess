package handlers;

import java.util.Optional;

import dataaccess.DatabaseManager;
import dataaccess.inmemory.InMemoryDatabase;
import io.javalin.http.Context;
import io.javalin.http.HandlerType;

public final class ClearDBHandler implements Handler {
    private final Optional<InMemoryDatabase> db;

    public ClearDBHandler() {
        db = Optional.empty();
    }

    public ClearDBHandler(InMemoryDatabase database) {
        db = Optional.of(database);
    }

    @Override
    public void execute(Context context) {
        try {
            if (db.isPresent()) {
                db.get().clearDb();
            } else {
                DatabaseManager.clearDb();
            }
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
