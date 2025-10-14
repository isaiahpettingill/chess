package dataaccess;

import java.util.Collection;
import java.util.HashSet;

import models.User;

public final class UserRepository implements Repository<User, Long> {
    private static HashSet<User> users = new HashSet<User>();

    @Override
    public Collection<User> list() {
        return users;
    }

    @Override
    public User get(Long Id) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'get'");
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
        users.add(model);
        return model;
    }

    @Override
    public User delete(Long Id) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'delete'");
    }

}
