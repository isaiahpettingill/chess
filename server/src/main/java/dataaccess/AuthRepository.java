package dataaccess;

import java.util.Collection;
import java.util.UUID;
import models.AuthToken;

public final class AuthRepository implements Repository<AuthToken, UUID> {

    @Override
    public Collection<AuthToken> list() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'list'");
    }

    @Override
    public AuthToken get(UUID Id) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'get'");
    }

    @Override
    public boolean exists(KeyGetter<AuthToken> getter) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'exists'");
    }

    @Override
    public AuthToken upsert(AuthToken model) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'upsert'");
    }

    @Override
    public AuthToken delete(UUID Id) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'delete'");
    }

}
