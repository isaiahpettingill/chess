package dataaccess.inmemory;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

import dataaccess.Repository;
import models.AuthToken;

public final class InMemoryAuthRepository implements Repository<AuthToken, UUID> {
    private InMemoryDatabase database;

    public InMemoryAuthRepository(InMemoryDatabase db) {
        database = db;
    }

    @Override
    public Collection<AuthToken> list() {
        return database.tokens();
    }

    @Override
    public Optional<AuthToken> get(UUID id) {
        return database.getToken(id);
    }

    @Override
    public boolean exists(KeyGetter<AuthToken> getter) {
        return list().stream().anyMatch(getter::where);
    }

    @Override
    public AuthToken upsert(AuthToken model) {
        if (database.getToken(model.authToken()).isPresent()){
            return model;
        }
        database.addToken(model);
        return model;
    }

    @Override
    public void delete(UUID id) {
        var token = get(id);
        if (token.isPresent()) {
            database.deleteToken(token.get());
        }
    }

    @Override
    public Optional<AuthToken> getBy(KeyGetter<AuthToken> getter) {
        return database.tokens.values().stream().filter(x -> getter.where(x)).findFirst();
    }

}
