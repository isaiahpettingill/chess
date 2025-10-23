package service;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.*;

import dataaccess.inmemory.InMemoryDatabase;
import dataaccess.inmemory.InMemoryGameRespository;
import dto.CreateGamePayload;
import dto.JoinGamePayload;

public class GameServiceTests {
    @Test
    public void listWorks(){
        final var db = new InMemoryDatabase();
        var repo = new InMemoryGameRespository(db);
        var gameService = new GameService(repo);    
        
        gameService.createGame(new CreateGamePayload(
            "The game"
        ));

        gameService.createGame(new CreateGamePayload(
            "Your mom"
        ));

        var games = gameService.listGames();
        assertEquals(2, games.size());
    }

    @Test
    public void listDoesNotErrorOnEdit(){
        final var db = new InMemoryDatabase();

        var repo = new InMemoryGameRespository(db);
        var gameService = new GameService(repo);    
        
        var game1 = gameService.createGame(new CreateGamePayload(
            "The game"
        ));

        var game2 = gameService.createGame(new CreateGamePayload(
            "Your mom"
        ));

        gameService.joinGame(new JoinGamePayload("WHITE", game2.id()), "your mom");

        gameService.joinGame(new JoinGamePayload("WHITE", game1.id()), "your mom");
        
        assertTrue(gameService.gameExists(game1.id()));
        assertTrue(gameService.gameExists(game2.id()));
        assertTrue(repo.get(game1.id()).isPresent());
        assertTrue(repo.get(game1.id()).get().whiteUsername().equals("your mom"));
    }

    @Test
    public void canJoinGame(){
        final var db = new InMemoryDatabase();

        var repo = new InMemoryGameRespository(db);
        var gameService = new GameService(repo);

        var game = gameService.createGame(new CreateGamePayload(
            "The game"
        ));

        gameService.joinGame(new JoinGamePayload("WHITE", game.id()), "jonesy");

        game = repo.get(game.id()).get();

        assertEquals(game.whiteUsername(), "jonesy");
    }

    @Test
    public void cannotJoinGameIfAlreadyTaken(){
        final var db = new InMemoryDatabase();

        var repo = new InMemoryGameRespository(db);
        var gameService = new GameService(repo);

        var game = gameService.createGame(new CreateGamePayload(
            "The game"
        ));

        gameService.joinGame(new JoinGamePayload("WHITE", game.id()), "jonesy");

        game = repo.get(game.id()).get();

        assertTrue(gameService.isPositionAlreadyTaken(new JoinGamePayload("WHITE", game.id()), "jefferson"));

        gameService.joinGame(new JoinGamePayload("WHITE", game.id()), "richard");
        
        assertEquals(game.whiteUsername(), "jonesy");
    }
}
