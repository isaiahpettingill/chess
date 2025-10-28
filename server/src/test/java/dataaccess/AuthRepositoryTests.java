package dataaccess;

import static org.junit.jupiter.api.Assertions.*;

import java.sql.SQLException;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import models.AuthToken;

public class AuthRepositoryTests {
    @Test
    public void canList() {
        final var repo = new AuthRepository();
        assertDoesNotThrow(repo::list);
    }

    @Test
    public void listingWorks() throws DataAccessException, SQLException {
        final var repo = new AuthRepository();
        var list = repo.list();
        assertNotNull(list);
    }

    @Test
    public void canInsertAuthToken() {
        final var repo = new AuthRepository();
        assertDoesNotThrow(() -> repo.upsert(new AuthToken(null, "bob", UUID.randomUUID())));
    }

    @Test
    public void cantEditAuthToken() throws DataAccessException, SQLException {
        final var repo = new AuthRepository();
        final var uuid = UUID.randomUUID();
        final var token = repo.upsert(new AuthToken(null, "steve", uuid));
        assertNotNull(token.id());
        assertThrows(DataAccessException.class,
                () -> repo.upsert(new AuthToken(token.id(), "james", uuid)));
    }

    @Test
    public void canGetAuthToken() throws DataAccessException, SQLException {
        final var repo = new AuthRepository();
        final var token = repo.upsert(new AuthToken(null, "bob", UUID.randomUUID()));
        assertNotNull(token);
        var token2 = repo.get(token.authToken());
        assertNotNull(token2);
    }
}
