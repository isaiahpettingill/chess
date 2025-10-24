package dataaccess.inmemory;

import java.util.Collection;
import java.util.Optional;

import dataaccess.Repository;
import models.User;

public final class InMemoryUserRepository implements Repository<User, Integer> {
    public final InMemoryDatabase database;

    public InMemoryUserRepository(InMemoryDatabase db) {
        database = db;
    }

    @Override
    public Collection<User> list() {
        return database.users();
    }

    @Override
    public Optional<User> get(Integer id) {
        return database.getUser(id);
    }

    @Override
    public boolean exists(KeyGetter<User> compare) {
        for (var user : list()) {
            if (compare.where(user)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public User upsert(User model) {
        database.addUser(model);
        return model;
    }

    @Override
    public void delete(Integer id) {
        database.deleteUser(null);
    }

    @Override
    public Optional<User> getBy(KeyGetter<User> getter) {
        return database.users.values().stream().filter(x -> getter.where(x)).findFirst();
    }

}
