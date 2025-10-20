package dataaccess;

import java.util.Collection;
import java.util.Optional;
import java.util.Random;

import models.Game;

public final class GameRepository implements Repository<Game, Long> {
    private InMemoryDatabase _database;

    public GameRepository() {
        _database = new InMemoryDatabase();
    }

    @Override
    public Collection<Game> list() {
        return _database.games();
    }

    @Override
    public Optional<Game> get(Long Id) {
        return _database.getGame(Id);
    }

    @Override
    public boolean exists(KeyGetter<Game> getter) {
        return _database.games()
                .stream()
                .filter(x -> getter.where(x))
                .findFirst()
                .isPresent();
    }

    @Override
    public Game upsert(Game model) {
        if (model.id() == null){
            model = new Game(
                new Random().nextLong(),
                model.gameName(),
                model.whiteUsername(),
                model.blackUsername(),
                model.game()
            );
        }
        _database.addGame(model);
        return model;
    }

    @Override
    public void delete(Long Id) {
        var game = get(Id);
        if (game.isPresent())
            _database.deleteGame(game.get());
    }

}
