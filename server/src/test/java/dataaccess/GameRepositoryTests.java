package dataaccess;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.SQLException;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import models.Game;

public class GameRepositoryTests {
    @BeforeEach
    public void setUp() throws SQLException, DataAccessException {
        DatabaseManager.clearDb();
    }

    @Test
    public void listThrowethNot() {
        final var repo = new GameRepository();
        assertDoesNotThrow(repo::list);
    }

    @Test
    public void listingWorks() throws DataAccessException, SQLException {
        final var repo = new GameRepository();
        var list = repo.list();
        assertNotNull(list);
    }

    @Test
    public void canInsertGame() {
        final var repo = new GameRepository();
        assertDoesNotThrow(() -> repo.upsert(new Game(
                null, "bob", "asdfasdfdsfa", "bob@bob.com", "")));
    }

    @Test
    public void canEditGame() throws DataAccessException, SQLException {
        final var repo = new GameRepository();
        final var token = repo.upsert(new Game(null, "steve", "asfdasdf", "steve@steve.com", ""));
        assertNotNull(token.id());
        assertDoesNotThrow(
                () -> repo.upsert(new Game(token.id(), "james", "asfddasdfadf", "steve@steve.com", "")));
        assertTrue(repo.exists(x -> x.gameName().equals("james")));

    }

    @Test
    public void canGetGame() throws DataAccessException, SQLException {
        final var repo = new GameRepository();
        final var Game = repo.upsert(new Game(null, "bob2", "asdfasdfasf", "asdf@asdf.adsf", ""));
        assertNotNull(Game);
        var Game2 = repo.get(Game.id());
        assertNotNull(Game2);
    }

    @Test
    public void canDelete() throws DataAccessException, SQLException {
        final var repo = new GameRepository();
        final var Game = repo.upsert(new Game(null, "bob3", "asdfasf", "asdfasdf", ""));
        assertDoesNotThrow(() -> repo.delete(Game.id()));
    }

    @Test
    public void cantDeleteFakeOne() throws DataAccessException, SQLException {
        final var repo = new GameRepository();
        final var id = -1;
        assertDoesNotThrow(() -> repo.delete(id));
    }

    @Test
    public void getByWorks() throws DataAccessException, SQLException {
        final var repo = new GameRepository();
        repo.upsert(new Game(null, "stevo2", "", "", ""));
        assertTrue(repo.getBy(x -> x.gameName().equals("stevo2")).isPresent());
    }

    @Test
    public void existWorks() throws DataAccessException, SQLException {
        final var repo = new GameRepository();
        final var uuid = UUID.randomUUID().toString();
        repo.upsert(new Game(null, uuid, uuid, uuid, ""));
        assertTrue(repo.exists(x -> x.gameName().equals(uuid)));
        assertFalse(repo.exists(x -> x.gameName().equals(UUID.randomUUID().toString())));
    }

    @Test
    public void existThrowethNot() throws DataAccessException, SQLException {
        final var repo = new GameRepository();
        assertDoesNotThrow(() -> repo.exists(x -> x.gameName().equals("geoffery")));
    }

    @Test
    public void emptyGetByDoesNotThrow() throws DataAccessException, SQLException {
        final var repo = new GameRepository();
        assertDoesNotThrow(() -> repo.getBy(x -> x.gameName().equals("Tyson")));
    }
}
