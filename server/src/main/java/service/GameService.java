package service;

import java.util.Collection;

import dataaccess.inmemory.InMemoryGameRespository;
import dto.CreateGamePayload;
import dto.JoinGamePayload;
import models.Game;

public final class GameService implements Service {
    private final InMemoryGameRespository gameRepository;

    public GameService(InMemoryGameRespository gameRepository) {
        this.gameRepository = gameRepository;
    }

    public Collection<Game> listGames() {
        return this.gameRepository.list();
    }

    public boolean gameExists(int gameId){
        return this.gameRepository.get(gameId).isPresent();
    }

    public boolean isPositionAlreadyTaken(JoinGamePayload payload, String username){
        final var game = this.gameRepository.get(payload.gameID());
        if (game.isPresent() && username != null){
            if (payload.playerColor().equals(JoinGamePayload.WHITE)){
                return game.get().whiteUsername() != null;
            }
            else {
                return game.get().blackUsername() != null;
            }
        }
        return true;
    }

    public void joinGame(JoinGamePayload payload, String username){
        final var game = this.gameRepository.get(payload.gameID());
        if (game.isPresent()){
            if (payload.playerColor().equals(JoinGamePayload.WHITE)){
                this.gameRepository.upsert(new Game(
                    game.get().id(),
                    game.get().gameName(),
                    username,
                    game.get().blackUsername(),
                    game.get().game()
                ));
            }
            else {
                this.gameRepository.upsert(new Game(
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
        return this.gameRepository.upsert(new Game(
            null,
            game.gameName(),
            null,
            null,
            null
        ));
    }
}
