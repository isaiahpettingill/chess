package dataaccess;

import java.util.Collection;

import models.Model;

public interface Repository<TModel extends Model, TKey> {
    static interface KeyGetter<T> {
        boolean where(T obj);
    }

    Collection<TModel> list();
    TModel get(TKey Id);
    boolean exists(KeyGetter<TModel> getter);
    TModel upsert(TModel model);
    TModel delete(TKey Id);
}
