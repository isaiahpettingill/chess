package dataaccess;

import java.util.Optional;
import java.util.UUID;
import models.AuthToken;

public final class AuthRepository extends Repository<AuthToken> {

    public Optional<AuthToken> getAuthToken(UUID id) { throw new RuntimeException("TODO"); }
}
