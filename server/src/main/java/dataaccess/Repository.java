package dataaccess;

import java.util.Collection;
import java.util.Optional;

import models.Model;

public interface Repository<T extends Model, K> {
    static interface KeyGetter<T> {
        boolean where(T obj);
    }

    Collection<T> list();

    Optional<T> get(K id);

    boolean exists(KeyGetter<T> getter);

    T upsert(T model);

    void delete(K id);
}
