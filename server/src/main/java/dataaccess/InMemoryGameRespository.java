package dataaccess;

import java.util.Collection;
import java.util.Optional;
import java.util.Random;

import models.Game;

public final class InMemoryGameRespository implements Repository<Game, Integer> {
    private InMemoryDatabase database;

    public InMemoryGameRespository(InMemoryDatabase db) {
        database = db;
    }

    @Override
    public Collection<Game> list() {
        return database.games();
    }

    @Override
    public Optional<Game> get(Integer id) {
        return database.getGame(id);
    }

    @Override
    public boolean exists(KeyGetter<Game> getter) {
        return database.games()
                .stream()
                .filter(x -> getter.where(x))
                .findFirst()
                .isPresent();
    }

    @Override
    public Game upsert(Game model) {
        var gameToAdd = model;
        if (model.id() == null){
            gameToAdd = new Game(
                (new Random().nextInt(0, Integer.MAX_VALUE)),
                model.gameName(),
                model.whiteUsername(),
                model.blackUsername(),
                model.game()
            );
        }
        database.addGame(gameToAdd);
        return gameToAdd;
    }

    @Override
    public void delete(Integer id) {
        var game = get(id);
        if (game.isPresent()) {
            database.deleteGame(game.get());
        }
    }

}
