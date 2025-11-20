package service;

import java.sql.SQLException;
import java.util.Collection;
import java.util.Optional;

import com.google.gson.Gson;

import chess.ChessGame;
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

    public void markFinished(Game game) throws DataAccessException, SQLException {
        this.gameRepository
                .upsert(new Game(game.id(), game.gameName(), game.whiteUsername(), game.blackUsername(), game.game(),
                        true));
    }

    public Optional<Game> getGame(int gameId) throws DataAccessException, SQLException {
        return this.gameRepository.get(gameId);
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

    public void removeUserFromGame(int gameID, String username) throws DataAccessException, SQLException {
        final var game = this.gameRepository.get(gameID);
        if (game.isPresent()) {
            if (game.get().whiteUsername() != null && game.get().whiteUsername().equals(username)) {
                this.gameRepository.upsert(new Game(
                        game.get().id(),
                        game.get().gameName(),
                        null,
                        game.get().blackUsername(),
                        game.get().game()));
            } else if (game.get().blackUsername() != null && game.get().blackUsername().equals(username)) {
                this.gameRepository.upsert(new Game(
                        game.get().id(),
                        game.get().gameName(),
                        game.get().whiteUsername(),
                        null,
                        game.get().game()));
            }
        }
    }

    public Game createGame(CreateGamePayload game) throws DataAccessException, SQLException {
        var thegame = new ChessGame();
        var thegamestring = new Gson().toJson(thegame);
        return this.gameRepository.upsert(new Game(
                null,
                game.gameName(),
                null,
                null,
                thegamestring));
    }

    public void updateGame(int gameID, ChessGame theGame) throws SQLException, DataAccessException {
        final var game = getGame(gameID).get();

        gameRepository.upsert(new Game(gameID, game.gameName(), game.whiteUsername(), game.blackUsername(),
                new Gson().toJson(game), game.isOver()));
    }
}
