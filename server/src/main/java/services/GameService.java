package services;

import java.util.Collection;

import dataaccess.GameRepository;
import dto.CreateGamePayload;
import models.Game;

public final class GameService implements Service {
    private final GameRepository _gameRepository;

    public GameService(GameRepository gameRepository) {
        _gameRepository = gameRepository;
    }

    public Collection<Game> listGames() {
        return _gameRepository.list();
    }

    public void joinGame(){
        
    }

    public Game createGame(CreateGamePayload game){
        return _gameRepository.upsert(new Game(
            null,
            game.gameName(),
            null,
            null,
            null
        ));
    }
}
