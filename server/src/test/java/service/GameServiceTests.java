package service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.*;

import dataaccess.InMemoryGameRespository;
import dto.CreateGamePayload;
import dto.JoinGamePayload;

public class GameServiceTests {
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

        gameService.joinGame(new JoinGamePayload("WHITE", game.id()), "richard");
        
        assertEquals(game.whiteUsername(), "jonesy");
    }
}
