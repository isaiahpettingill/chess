package dataaccess;

import java.util.Collection;
import java.util.Optional;

import models.User;

public final class InMemoryUserRepository implements Repository<User, Integer> {
    private InMemoryDatabase database;

    public InMemoryUserRepository() {
        database = new InMemoryDatabase();
    }

    @Override
    public Collection<User> list() {
        return database.users();
    }

    @Override
    public Optional<User> get(Integer id) {
        return database.getUser(id);
    }

    public Optional<User> getByUsername(String username){
        for (var user : list()) {
            if (user.username().equals(username)) {
                return Optional.of(user);
            }
        }
        return Optional.empty();
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

}
