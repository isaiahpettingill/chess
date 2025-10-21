package services;

import java.util.Collection;

import dataaccess.InMemoryGameRespository;
import dto.CreateGamePayload;
import dto.JoinGamePayload;
import models.Game;

public final class GameService implements Service {
    private final InMemoryGameRespository _gameRepository;

    public GameService(InMemoryGameRespository gameRepository) {
        _gameRepository = gameRepository;
    }

    public Collection<Game> listGames() {
        return _gameRepository.list();
    }

    public boolean gameExists(int gameId){
        return _gameRepository.get(gameId).isPresent();
    }

    public boolean isPositionAlreadyTaken(JoinGamePayload payload, String username){
        final var game = _gameRepository.get(payload.gameID());
        if (game.isPresent() && username != null){
            if (payload.playerColor().equals(JoinGamePayload.WHITE)){
                return game.get().whiteUsername() != null || username.equals(game.get().blackUsername());
            }
            else {
                return game.get().blackUsername() != null || username.equals(game.get().whiteUsername());
            }
        }
        return true;
    }

    public void joinGame(JoinGamePayload payload, String username){
        final var game = _gameRepository.get(payload.gameID());
        if (game.isPresent()){
            if (payload.playerColor().equals(JoinGamePayload.WHITE)){
                _gameRepository.upsert(new Game(
                    game.get().id(),
                    game.get().gameName(),
                    username,
                    game.get().blackUsername(),
                    game.get().game()
                ));
            }
            else {
                _gameRepository.upsert(new Game(
                    game.get().id(),
                    game.get().gameName(),
                    game.get().whiteUsername(),
                    username,
                    game.get().game()
                ));
            }
        }
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
