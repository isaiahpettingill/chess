package dataaccess;

import java.util.Collection;
import java.util.Optional;

import models.Model;

public interface Repository<TModel extends Model, TKey> {
    static interface KeyGetter<T> {
        boolean where(T obj);
    }

    Collection<TModel> list();
    Optional<TModel> get(TKey Id);
    boolean exists(KeyGetter<TModel> getter);
    TModel upsert(TModel model);
    void delete(TKey Id);
}
