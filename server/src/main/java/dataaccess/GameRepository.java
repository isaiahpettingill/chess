package dataaccess;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;
import java.util.Set;

import models.Game;

public class GameRepository extends AbstractRepository<Game> {

    @Override
    public Collection<Game> list() throws DataAccessException, SQLException {
        try (final var connection = DatabaseManager.getConnection()) {
            final var statement = connection.prepareStatement(
                    "select gameId, gameName, whiteUsername, blackUsername, isOver, game from games;");
            final var result = statement.executeQuery();
            final var users = new ArrayList<Game>();

            while (result.next()) {
                final var id = result.getInt("gameId");
                final var gameName = result.getString("gameName");
                final var whiteUsername = result.getString("whiteUsername");
                final var blackUsername = result.getString("blackUsername");
                final var game = result.getString("game");
                final var isOver = result.getBoolean("isOver");

                users.add(new Game(id, gameName, whiteUsername, blackUsername, game, isOver));
            }

            return users;
        } catch (Exception ex) {
            return Set.of();
        }
    }

    @Override
    public Optional<Game> get(Integer id) throws DataAccessException, SQLException {
        try (final var connection = DatabaseManager.getConnection()) {
            final var statement = connection.prepareStatement(
                    "select gameId, gameName, whiteUsername, blackUsername, game, isOver from games where gameId = ?;");
            statement.setInt(1, id);
            final var result = statement.executeQuery();

            if (!result.next()) {
                return Optional.empty();
            }

            final var theId = result.getInt("gameId");
            final var gameName = result.getString("gameName");
            final var whiteUsername = result.getString("whiteUsername");
            final var blackUsername = result.getString("blackUsername");
            final var game = result.getString("game");
            final var isOver = result.getBoolean("isOver");

            return Optional.of(new Game(theId, gameName, whiteUsername, blackUsername, game, isOver));
        }
    }

    @Override
    public boolean exists(KeyGetter<Game> getter) throws DataAccessException, SQLException {
        return list().stream().anyMatch(getter::where);
    }

    protected Game insert(Game game) throws DataAccessException, SQLException {
        try (final var connection = DatabaseManager.getConnection()) {
            final var query = "insert into games(gameName, whiteUsername, blackUsername, game) values (?, ?, ?, ?)";
            final var statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            statement.setString(1, game.gameName());
            statement.setString(2, game.whiteUsername());
            statement.setString(3, game.blackUsername());
            statement.setString(4, game.game());
            statement.executeUpdate();
            final var keys = statement.getGeneratedKeys();
            keys.next();
            final var id = keys.getInt(1);
            return new Game(id, game.gameName(), game.whiteUsername(), game.blackUsername(), game.game());
        }
    }

    protected Game update(Game game) throws DataAccessException, SQLException {
        try (final var connection = DatabaseManager.getConnection()) {
            final var statement = connection
                    .prepareStatement(
                            "update games set gameName = ?, whiteUsername = ?, blackUsername = ?, game = ?, isOver = ? where gameId = ?");
            statement.setString(1, game.gameName());
            statement.setString(2, game.whiteUsername());
            statement.setString(3, game.blackUsername());
            statement.setString(4, game.game());
            statement.setBoolean(5, game.isOver());
            statement.setInt(6, game.id());
            statement.executeUpdate();
            return game;
        }
    }

    @Override
    public void delete(Integer id) throws DataAccessException, SQLException {
        try (final var connection = DatabaseManager.getConnection()) {
            final var statement = connection
                    .prepareStatement(
                            "delete from games where gameId = ?");
            statement.setInt(1, id);
            statement.execute();
        }
    }

    @Override
    public Optional<Game> getBy(KeyGetter<Game> getter) throws DataAccessException, SQLException {
        return list().stream().filter(getter::where).findFirst();
    }

}
