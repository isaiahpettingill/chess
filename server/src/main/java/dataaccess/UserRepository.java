package dataaccess;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;
import java.util.Set;

import models.User;

public class UserRepository implements Repository<User, Integer> {
    @Override
    public Collection<User> list() throws DataAccessException, SQLException {
        try (final var connection = DatabaseManager.getConnection()) {
            final var statement = connection.prepareStatement("select userId, username, passwordHash, emailAddress from users;");
            final var result = statement.executeQuery();
            final var users = new ArrayList<User>();

            while (result.next()) {
                final var id = result.getInt("userId");
                final var username = result.getString("username");
                final var passwordHash = result.getString("passwordHash");
                final var emailAddress = result.getString("emailAddress");
                users.add(new User(id, username, passwordHash, emailAddress));
            }

            return users;
        } catch (Exception ex) {
            return Set.of();
        }
    }

    @Override
    public Optional<User> get(Integer id) throws DataAccessException, SQLException {
        try (final var connection = DatabaseManager.getConnection()) {
            final var statement = connection.prepareStatement("select userId, username, passwordHash, emailAddress from users where userId = ?;");
            statement.setInt(1, id);
            final var result = statement.executeQuery();

            if (!result.next()) {
                return Optional.empty();
            }

            final var theId = result.getInt("userId");
            final var username = result.getString("username");
            final var passwordHash = result.getString("passwordHash");
            final var emailAddress = result.getString("emailAddress");

            return Optional.of(new User(theId, username, passwordHash, emailAddress));
        }
    }

    @Override
    public boolean exists(KeyGetter<User> getter) throws DataAccessException, SQLException {
        return list().stream().anyMatch(getter::where);

    }

    @Override
    public User upsert(User model) throws DataAccessException, SQLException {
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

    private User insert(User user) throws DataAccessException, SQLException {
        try (final var connection = DatabaseManager.getConnection()) {
            final var statement = connection
                    .prepareStatement("insert into users(username, passwordHash, emailAddress) values (?, ?, ?)",
                            Statement.RETURN_GENERATED_KEYS);
            statement.setString(1, user.username());
            statement.setString(2, user.passwordHash());
            statement.setString(3, user.emailAddress());
            statement.executeUpdate();
            final var keys = statement.getGeneratedKeys();
            keys.next();
            final var id = keys.getInt(1);
            return new User(id, user.username(), user.passwordHash(), user.emailAddress());
        }
    }

    private User update(User user) throws DataAccessException, SQLException {
        try (final var connection = DatabaseManager.getConnection()) {
            final var statement = connection
                    .prepareStatement(
                            "update users set username = ?, passwordHash = ?, emailAddress = ? where userId = ?");
            statement.setString(1, user.username());
            statement.setString(2, user.passwordHash());
            statement.setString(3, user.emailAddress());
            statement.setInt(4, user.id());
            statement.executeUpdate();
            return user;
        }
    }

    @Override
    public void delete(Integer id) throws DataAccessException, SQLException {
        try (final var connection = DatabaseManager.getConnection()) {
            final var statement = connection.prepareStatement("delete from users where userId = ?;");
            statement.setInt(1, id);
            statement.execute();
        }
    }

    @Override
    public Optional<User> getBy(KeyGetter<User> getter) throws DataAccessException, SQLException {
        return list().stream().filter(getter::where).findFirst();
    }

}
