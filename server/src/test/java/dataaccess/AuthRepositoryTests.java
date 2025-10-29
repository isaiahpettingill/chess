package dataaccess;

import static org.junit.jupiter.api.Assertions.*;

import java.sql.SQLException;
import java.util.UUID;

import org.junit.jupiter.api.*;

import models.AuthToken;

public class AuthRepositoryTests {
    @BeforeAll
    public void setUp() throws SQLException, DataAccessException {
        DatabaseManager.clearDb();
    }

    @Test
    public void listThrowethNot() {
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

    @Test
    public void canDelete() throws DataAccessException, SQLException {
        final var repo = new AuthRepository();
        final var uuid = UUID.randomUUID();
        repo.upsert(new AuthToken(null, "bob", uuid));
        assertDoesNotThrow(() -> repo.delete(uuid));
    }

    @Test
    public void cantDeleteFakeOne() throws DataAccessException, SQLException {
        final var repo = new AuthRepository();
        final var uuid = UUID.randomUUID();
        assertDoesNotThrow(() -> repo.delete(uuid));
    }

    @Test
    public void getByWorks() throws DataAccessException, SQLException {
        final var repo = new AuthRepository();
        final var uuid = UUID.randomUUID();
        repo.upsert(new AuthToken(null, "stevo", uuid));
        assertTrue(repo.getBy(x -> x.username().equals("stevo")).isPresent());
    }

    @Test
    public void existWorks() throws DataAccessException, SQLException {
        final var repo = new AuthRepository();
        final var uuid = UUID.randomUUID();
        repo.upsert(new AuthToken(null, "stephan", uuid));
        assertTrue(repo.exists(x -> x.username().equals("stephan")));
        assertFalse(repo.exists(x -> x.username().equals("geoffery")));
    }

    @Test
    public void existThrowethNot() throws DataAccessException, SQLException {
        final var repo = new AuthRepository();
        assertDoesNotThrow(() -> repo.exists(x -> x.username().equals("geoffery")));
    }

    @Test
    public void emptyGetByDoesNotThrow() throws DataAccessException, SQLException {
        final var repo = new AuthRepository();
        assertDoesNotThrow(() -> repo.getBy(x -> x.username().equals("Tyson")));
    }

}
