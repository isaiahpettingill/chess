package service;

import static org.junit.jupiter.api.Assertions.*;

import java.sql.SQLException;

import org.junit.jupiter.api.*;

import dataaccess.DataAccessException;
import dataaccess.inmemory.InMemoryDatabase;
import dataaccess.inmemory.InMemoryGameRespository;
import dto.CreateGamePayload;
import dto.JoinGamePayload;
import util.GameIdEncoder;

public class GameServiceTests {
    @Test
    public void listWorks() throws DataAccessException, SQLException {
        final var db = new InMemoryDatabase();
        var repo = new InMemoryGameRespository(db);
        var gameService = new GameService(repo);

        gameService.createGame(new CreateGamePayload(
                "The game"));

        gameService.createGame(new CreateGamePayload(
                "Your mom"));

        var games = gameService.listGames();
        assertEquals(2, games.size());
    }

    @Test
    public void listDoesNotErrorOnEdit() throws DataAccessException, SQLException {
        final var db = new InMemoryDatabase();

        var repo = new InMemoryGameRespository(db);
        var gameService = new GameService(repo);

        var game1 = gameService.createGame(new CreateGamePayload(
                "The game"));

        var game2 = gameService.createGame(new CreateGamePayload(
                "Your mom"));

        gameService.joinGame(new JoinGamePayload("WHITE", GameIdEncoder.encode(game2.id())), "your mom");

        gameService.joinGame(new JoinGamePayload("WHITE", GameIdEncoder.encode(game1.id())), "your mom");

        assertTrue(gameService.gameExists(game1.id()));
        assertTrue(gameService.gameExists(game2.id()));
        assertTrue(repo.get(game1.id()).isPresent());
        assertTrue(repo.get(game1.id()).get().whiteUsername().equals("your mom"));
    }

    @Test
    public void canJoinGame() throws DataAccessException, SQLException {
        final var db = new InMemoryDatabase();

        var repo = new InMemoryGameRespository(db);
        var gameService = new GameService(repo);

        var game = gameService.createGame(new CreateGamePayload(
                "The game"));

        gameService.joinGame(new JoinGamePayload("WHITE", GameIdEncoder.encode(game.id())), "jonesy");

        game = repo.get(game.id()).get();

        assertEquals(game.whiteUsername(), "jonesy");
    }

    @Test
    public void cannotJoinGameIfAlreadyTaken() throws DataAccessException, SQLException {
        final var db = new InMemoryDatabase();

        var repo = new InMemoryGameRespository(db);
        var gameService = new GameService(repo);

        var game = gameService.createGame(new CreateGamePayload(
                "The game"));

        gameService.joinGame(new JoinGamePayload("WHITE", GameIdEncoder.encode(game.id())), "jonesy");

        game = repo.get(game.id()).get();

        assertTrue(gameService.isPositionAlreadyTaken(new JoinGamePayload("WHITE", GameIdEncoder.encode(game.id())), "jefferson"));

        gameService.joinGame(new JoinGamePayload("WHITE", GameIdEncoder.encode(game.id())), "richard");

        assertEquals(game.whiteUsername(), "jonesy");
    }
}
