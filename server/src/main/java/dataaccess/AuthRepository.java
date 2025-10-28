package dataaccess;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

import models.AuthToken;

public class AuthRepository implements Repository<AuthToken, UUID> {

    @Override
    public Collection<AuthToken> list() throws DataAccessException, SQLException {
        try (final var connection = DatabaseManager.getConnection()) {
            final var statement = connection.prepareStatement("""
                    select tokenId, username, token
                    from authTokens;
                    ;""");
            final var result = statement.executeQuery();
            final var tokens = new ArrayList<AuthToken>();

            while (result.next()) {
                final var id = result.getInt(1);
                final var username = result.getString(2);
                final var token = result.getString(3);
                tokens.add(new AuthToken(id, username, UUID.fromString(token)));
            }
            return tokens;
        }
    }

    @Override
    public Optional<AuthToken> get(UUID id) throws DataAccessException, SQLException {
        try (final var connection = DatabaseManager.getConnection()) {
            final var statement = connection.prepareStatement(" select tokenId, username, token from authTokens where token = ?;");
            statement.setString(1, id.toString());
            final var result = statement.executeQuery();

            if (!result.next()) {
                return Optional.empty();
            }

            final var theId = result.getInt(1);
            final var username = result.getString(2);
            final var token = result.getString(3);

            return Optional.of(new AuthToken(theId, username, UUID.fromString(token)));
        }
    }

    @Override
    public boolean exists(KeyGetter<AuthToken> getter) throws DataAccessException, SQLException {
        return list().stream().anyMatch(getter::where);
    }

    @Override
    public AuthToken upsert(AuthToken model) throws DataAccessException, SQLException {
        if (model.id() != null) {
            final var existing = get(model.authToken());
            if (existing.isPresent()) {
                return update(model);
            } else {
                return insert(model);
            }
        } else {
            return insert(model);
        }
    }

    private AuthToken insert(AuthToken authToken) throws DataAccessException, SQLException {
        try (final var connection = DatabaseManager.getConnection()) {
            final var statement = connection
                    .prepareStatement("insert into authTokens(token, username) values (?, ?)",
                            Statement.RETURN_GENERATED_KEYS);
            statement.setString(1, authToken.authToken().toString());
            statement.setString(2, authToken.username());
            statement.executeUpdate();
            final var keys = statement.getGeneratedKeys();
            keys.next();
            final var id = keys.getInt(1);
            return new AuthToken(id, authToken.username(), authToken.authToken());
        }
    }

    private AuthToken update(AuthToken authToken) throws DataAccessException, SQLException {
        throw new DataAccessException("AuthTokens are immutable!");
    }

    @Override
    public void delete(UUID id) throws DataAccessException, SQLException {
        try (final var connection = DatabaseManager.getConnection()) {
            final var statement = connection
                    .prepareStatement(
                            "delete from authTokens where token = ?");
            statement.setString(1, id.toString());
            statement.execute();
        }
    }

    @Override
    public Optional<AuthToken> getBy(KeyGetter<AuthToken> getter) throws DataAccessException, SQLException {
        return list().stream().filter(getter::where).findFirst();
    }

}
