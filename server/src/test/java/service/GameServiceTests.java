package service;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.*;

import dataaccess.InMemoryGameRespository;
import dto.CreateGamePayload;
import dto.JoinGamePayload;

public class GameServiceTests {
    @Test
    public void listWorks(){
        var repo = new InMemoryGameRespository();
        var gameService = new GameService(repo);    
        
        gameService.createGame(new CreateGamePayload(
            "The game"
        ));

        gameService.createGame(new CreateGamePayload(
            "Your mom"
        ));

        var games = gameService.listGames();
        assertEquals(games.size(), 2);
    }

    @Test
    public void canJoinGame(){
        var repo = new InMemoryGameRespository();
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
        var repo = new InMemoryGameRespository();
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
