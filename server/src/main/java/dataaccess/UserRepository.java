package dataaccess;

import java.util.Collection;
import java.util.Optional;

import models.User;

public final class UserRepository implements Repository<User, Long> {
    private InMemoryDatabase _database;
    public UserRepository(){
        _database = new InMemoryDatabase();
    }

    @Override
    public Collection<User> list() {
        return _database.users();
    }

    @Override
    public Optional<User> get(Long Id) {
        return _database.getUser(Id);
    }

    @Override
    public boolean exists(KeyGetter<User> compare) {
        for (var user : list()){
            if (compare.where(user)){
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
    public void delete(Long Id) {
        _database.deleteUser(null);
    }

}
