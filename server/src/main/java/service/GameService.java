package service;

import java.sql.SQLException;
import java.util.Collection;

import dataaccess.DataAccessException;
import dataaccess.Repository;
import dto.CreateGamePayload;
import dto.JoinGamePayload;
import models.Game;

public final class GameService {
    private final Repository<Game, Integer> gameRepository;

    public GameService(Repository<Game, Integer> gameRepository) {
        this.gameRepository = gameRepository;
    }

    public Collection<Game> listGames() throws DataAccessException, SQLException {
        return this.gameRepository.list();
    }

    public boolean gameExists(int gameId) throws DataAccessException, SQLException {
        return this.gameRepository.get(gameId).isPresent();
    }

    public boolean isPositionAlreadyTaken(JoinGamePayload payload, String username)
            throws DataAccessException, SQLException {
        final var game = this.gameRepository.get(payload.gameID());
        if (game.isPresent() && username != null) {
            if (payload.playerColor().equals(JoinGamePayload.WHITE)) {
                return game.get().whiteUsername() != null;
            } else {
                return game.get().blackUsername() != null;
            }
        }
        return true;
    }

    public void joinGame(JoinGamePayload payload, String username) throws DataAccessException, SQLException {
        final var game = this.gameRepository.get(payload.gameID());
        if (game.isPresent()) {
            if (payload.playerColor().equals(JoinGamePayload.WHITE)) {
                this.gameRepository.upsert(new Game(
                        game.get().id(),
                        game.get().gameName(),
                        username,
                        game.get().blackUsername(),
                        game.get().game()));
            } else {
                this.gameRepository.upsert(new Game(
                        game.get().id(),
                        game.get().gameName(),
                        game.get().whiteUsername(),
                        username,
                        game.get().game()));
            }
        }
    }

    public Game createGame(CreateGamePayload game) throws DataAccessException, SQLException {
        return this.gameRepository.upsert(new Game(
                null,
                game.gameName(),
                null,
                null,
                null));
    }
}
