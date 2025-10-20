package dataaccess;

import java.util.Collection;
import java.util.Optional;

import models.User;

public final class UserRepository implements Repository<User, Integer> {
    private InMemoryDatabase _database;

    public UserRepository() {
        _database = new InMemoryDatabase();
    }

    @Override
    public Collection<User> list() {
        return _database.users();
    }

    @Override
    public Optional<User> get(Integer Id) {
        return _database.getUser(Id);
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
        _database.addUser(model);
        return model;
    }

    @Override
    public void delete(Integer Id) {
        _database.deleteUser(null);
    }

}
