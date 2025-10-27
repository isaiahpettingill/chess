package dataaccess;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;
import java.util.Set;

import models.Game;

public class GameRepository implements Repository<Game, Integer> {

    @Override
    public Collection<Game> list() throws DataAccessException, SQLException  {
        try (final var connection = DatabaseManager.getConnection()){
            final var statement = connection.prepareStatement("select * from users;");
            final var result = statement.executeQuery();
            final var users = new ArrayList<Game>();

            while (result.next()){
                final var id = result.getInt("gameId");
                final var gameName = result.getString("gameName");
                final var whiteUsername = result.getString("whiteUsername");
                final var blackUsername = result.getString("blackUsername");
                final var game = result.getString("game");
                users.add(new Game(id, gameName, whiteUsername, blackUsername, game));
            }

            return users;
        }
        catch (Exception ex){
            return Set.of();
        }
    }

    @Override
    public Optional<Game> get(Integer id) throws DataAccessException, SQLException  {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'get'");
    }

    @Override
    public boolean exists(KeyGetter<Game> getter) throws DataAccessException, SQLException  {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'exists'");
    }

    @Override
    public Game upsert(Game model) throws DataAccessException, SQLException  {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'upsert'");
    }

    @Override
    public void delete(Integer id) throws DataAccessException, SQLException  {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'delete'");
    }

    @Override
    public Optional<Game> getBy(KeyGetter<Game> getter) throws DataAccessException, SQLException  {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getBy'");
    }
    
}
