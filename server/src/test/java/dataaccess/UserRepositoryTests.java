package dataaccess;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.SQLException;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import models.User;

public class UserRepositoryTests {
    @Test
    public void listThrowethNot() {
        final var repo = new UserRepository();
        assertDoesNotThrow(repo::list);
    }

    @Test
    public void listingWorks() throws DataAccessException, SQLException {
        final var repo = new UserRepository();
        var list = repo.list();
        assertNotNull(list);
    }

    @Test
    public void canInsertUser() {
        final var uuid = UUID.randomUUID().toString();
        final var repo = new UserRepository();
        assertDoesNotThrow(() -> repo.upsert(new User(
                null, uuid, "asdfasdfdsfa", "bob@bob.com")));
    }

    @Test
    public void cantInsertDuplicateUser() throws DataAccessException, SQLException {
        final var repo = new UserRepository();
        final var uuid = UUID.randomUUID().toString();
        repo.upsert(new User(
                null, uuid, "asdfasdfdsfa", "bob@bob.com"));
        assertThrows(Exception.class, () -> repo.upsert(new User(
                null, uuid, "asdfasdfdsfa", "bob@bob.com")));
    }

    @Test
    public void canEditUser() throws DataAccessException, SQLException {
        final var repo = new UserRepository();
        final var uuid1 = UUID.randomUUID().toString();
        final var uuid2 = UUID.randomUUID().toString();

        final var token = repo.upsert(new User(null, uuid1, "asfdasdf", "steve@steve.com"));
        assertNotNull(token.id());
        assertDoesNotThrow(
                () -> repo.upsert(new User(token.id(), uuid2, "asfddasdfadf", "steve@steve.com")));
        assertTrue(repo.exists(x -> x.username().equals(uuid2)));

    }

    @Test
    public void canGetUser() throws DataAccessException, SQLException {
        final var repo = new UserRepository();
        final var uuid1 = UUID.randomUUID().toString();
        final var user = repo.upsert(new User(null, uuid1, "asdfasdfasf", "asdf@asdf.adsf"));
        assertNotNull(user);
        var user2 = repo.get(user.id());
        assertNotNull(user2);
    }

    @Test
    public void cantGetFakeUser() throws DataAccessException, SQLException {
        final var repo = new UserRepository();
        final var id1 = -1;
        var user2 = repo.get(id1);
        assertTrue(user2.isEmpty());
    }

    @Test
    public void canDelete() throws DataAccessException, SQLException {
        final var repo = new UserRepository();
        final var uuid1 = UUID.randomUUID().toString();
        final var user = repo.upsert(new User(null, uuid1, "asdfasf", "asdfasdf"));
        assertDoesNotThrow(() -> repo.delete(user.id()));
    }

    @Test
    public void cantDeleteFakeOne() throws DataAccessException, SQLException {
        final var repo = new UserRepository();
        final var id = -1;
        assertDoesNotThrow(() -> repo.delete(id));
    }

    @Test
    public void getByWorks() throws DataAccessException, SQLException {
        final var repo = new UserRepository();
        final var uuid1 = UUID.randomUUID().toString();
        repo.upsert(new User(null, uuid1, "", ""));
        assertTrue(repo.getBy(x -> x.username().equals(uuid1)).isPresent());
    }

    @Test
    public void existWorks() throws DataAccessException, SQLException {
        final var repo = new UserRepository();
        final var uuid = UUID.randomUUID().toString();
        repo.upsert(new User(null, uuid, uuid, uuid));
        assertTrue(repo.exists(x -> x.username().equals(uuid)));
        assertFalse(repo.exists(x -> x.username().equals(UUID.randomUUID().toString())));
    }

    @Test
    public void existThrowethNot() throws DataAccessException, SQLException {
        final var repo = new UserRepository();
        final var uuid1 = UUID.randomUUID().toString();
        assertDoesNotThrow(() -> repo.exists(x -> x.username().equals(uuid1)));
    }

    @Test
    public void emptyGetByDoesNotThrow() throws DataAccessException, SQLException {
        final var repo = new UserRepository();
        final var uuid1 = UUID.randomUUID().toString();
        assertDoesNotThrow(() -> repo.getBy(x -> x.username().equals(uuid1)));
    }
}
