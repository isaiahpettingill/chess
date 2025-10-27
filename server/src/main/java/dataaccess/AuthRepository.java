package dataaccess;

import java.sql.SQLException;
import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

import models.AuthToken;

public class AuthRepository implements Repository<AuthToken, UUID> {

    @Override
    public Collection<AuthToken> list() throws DataAccessException, SQLException  {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'list'");
    }

    @Override
    public Optional<AuthToken> get(UUID id) throws DataAccessException, SQLException  {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'get'");
    }

    @Override
    public boolean exists(KeyGetter<AuthToken> getter) throws DataAccessException, SQLException  {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'exists'");
    }

    @Override
    public AuthToken upsert(AuthToken model) throws DataAccessException, SQLException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'upsert'");
    }

    @Override
    public void delete(UUID id) throws DataAccessException, SQLException  {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'delete'");
    }

    @Override
    public Optional<AuthToken> getBy(KeyGetter<AuthToken> getter) throws DataAccessException, SQLException  {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getBy'");
    }
    
}
