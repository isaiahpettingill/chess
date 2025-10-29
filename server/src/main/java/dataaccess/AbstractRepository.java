package dataaccess;

import java.sql.SQLException;

import models.Model;

public abstract class AbstractRepository<T extends Model> implements Repository<T, Integer> {

    protected abstract T insert(T model) throws DataAccessException, SQLException;

    protected abstract T update(T model) throws DataAccessException, SQLException;

    @Override
    public T upsert(T model) throws DataAccessException, SQLException {
        if (model.id() != null) {
            final var existing = get(model.id());
            if (existing.isPresent()) {
                return update(model);
            } else {
                return insert(model);
            }
        } else {
            return insert(model);
        }
    }
}