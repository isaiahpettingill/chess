package dataaccess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;
import java.util.Set;

import models.User;

public class UserRepository implements Repository<User, Integer> {
    @Override
    public Collection<User> list() {
        try (final var connection = DatabaseManager.getConnection()){
            final var statement = connection.prepareStatement("select * from users;");
            final var result = statement.executeQuery();
            final var users = new ArrayList<User>();

            while (result.next()){
                final var id = result.getInt("userId");
                final var username = result.getString("username");
                final var passwordHash = result.getString("passwordHash");
                final var emailAddress = result.getString("emailAddress");
                users.add(new User(id, username, passwordHash, emailAddress));
            }

            return users;
        }
        catch (Exception ex){
            return Set.of();
        }
    }

    @Override
    public Optional<User> get(Integer id) {
        try (final var connection = DatabaseManager.getConnection()){
            final var statement = connection.prepareStatement("select * from users where userId = ?;");
            statement.setInt(1, id);
            final var result = statement.executeQuery();

            result.next();
            final var theId = result.getInt("userId");
            final var username = result.getString("username");
            final var passwordHash = result.getString("passwordHash");
            final var emailAddress = result.getString("emailAddress");

            return Optional.of(new User(theId, username, passwordHash, emailAddress));

        }
        catch (Exception ex){
            return Optional.empty();
        }
    }

    @Override
    public boolean exists(KeyGetter<User> getter) {
        return list().stream().anyMatch(getter::where);

    }

    @Override
    public User upsert(User model) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'upsert'");
    }

    @Override
    public void delete(Integer id) {
        try (final var connection = DatabaseManager.getConnection()){
            final var statement = connection.prepareStatement("delete from users where userId = ?;");
            statement.setInt(1, id);
            final var result = statement.execute();

        }
        catch (Exception ex){
        }
    }

    @Override
    public Optional<User> getBy(KeyGetter<User> getter) {
        return list().stream().filter(getter::where).findFirst();
    }
    
}
