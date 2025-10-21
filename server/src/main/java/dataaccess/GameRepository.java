package dataaccess;

import java.util.Collection;
import java.util.Optional;
import java.util.Random;

import models.Game;

public final class GameRepository implements Repository<Game, Integer> {
    private InMemoryDatabase _database;

    public GameRepository() {
        _database = new InMemoryDatabase();
    }

    @Override
    public Collection<Game> list() {
        return _database.games();
    }

    @Override
    public Optional<Game> get(Integer Id) {
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
        if (exists(x -> x.id().equals(model.id()))){
            _database.deleteGame(get(model.id()).get());
        }
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
        _database.addGame(gameToAdd);
        return gameToAdd;
    }

    @Override
    public void delete(Integer Id) {
        var game = get(Id);
        if (game.isPresent())
            _database.deleteGame(game.get());
    }

}
