package dataaccess;

import java.sql.SQLException;
import java.util.Collection;
import java.util.Optional;

import models.Model;

public interface Repository<T extends Model, K> {
    static interface KeyGetter<T> {
        boolean where(T obj);
    }

    Collection<T> list() throws DataAccessException, SQLException ;

    Optional<T> get(K id) throws DataAccessException, SQLException ;
    Optional<T> getBy(KeyGetter<T> getter) throws DataAccessException, SQLException ;

    boolean exists(KeyGetter<T> getter) throws DataAccessException, SQLException ;

    T upsert(T model) throws DataAccessException, SQLException ;

    void delete(K id) throws DataAccessException, SQLException ;
}
