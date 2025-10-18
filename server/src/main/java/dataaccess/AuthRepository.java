package dataaccess;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;
import models.AuthToken;

public final class AuthRepository implements Repository<AuthToken, UUID> {
    private InMemoryDatabase _database;

    public AuthRepository() {
        _database = new InMemoryDatabase();
    }

    @Override
    public Collection<AuthToken> list() {
        return _database.tokens();
    }

    @Override
    public Optional<AuthToken> get(UUID Id) {
        return _database.getToken(Id);
    }

    @Override
    public boolean exists(KeyGetter<AuthToken> getter) {
        return list().stream().anyMatch(getter::where);
    }

    @Override
    public AuthToken upsert(AuthToken model) {
        _database.addToken(model);
        return model;
    }

    @Override
    public void delete(UUID Id) {
        var token = get(Id);
        if (token.isPresent())
            _database.deleteToken(token.get());
    }

}
