package dataaccess;

import java.util.Collection;

import models.Game;

public final class GameRepository implements Repository<Game, Long> {

    @Override
    public Collection<Game> list() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'list'");
    }

    @Override
    public Game get(Long Id) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'get'");
    }

    @Override
    public boolean exists(KeyGetter<Game> getter) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'exists'");
    }

    @Override
    public Game upsert(Game model) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'upsert'");
    }

    @Override
    public Game delete(Long Id) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'delete'");
    }

}
