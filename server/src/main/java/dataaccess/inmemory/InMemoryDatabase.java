package dataaccess.inmemory;

import java.util.Collection;
import java.util.HashMap;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import models.*;

public class InMemoryDatabase {
    public void clearDb() {
        tokens = new HashMap<>();
        games = new HashMap<>();
        users = new HashMap<>();
        usersByUsername = new HashMap<>();
    }

    public InMemoryDatabase() {
        tokens = new HashMap<>();
        games = new HashMap<>();
        users = new HashMap<>();
        usersByUsername = new HashMap<>();
    }

    public void addToken(AuthToken token) {
        tokens.put(token.authToken(), token);
    }

    public void deleteToken(AuthToken token) {
        tokens.remove(token.authToken());
    }

    public Optional<AuthToken> getToken(UUID token) {
        return tokens.get(token) == null ? Optional.empty() : Optional.of(tokens.get(token));
    }

    public void addGame(Game game) {
        games.put(game.id(), game);
    }

    public void deleteGame(Game game) {
        games.remove(game.id());
    }

    public Optional<Game> getGame(Integer gameId) {
        return games.get(gameId) == null ? Optional.empty() : Optional.of(games.get(gameId));
    }

    public Optional<User> getUser(Integer userId) {
        return users.get(userId) == null ? Optional.empty() : Optional.of(users.get(userId));
    }

    public void addUser(User user) {
        usersByUsername.put(user.username(), user);
        users.put(user.id(), user);
    }

    public void deleteUser(User user) {
        usersByUsername.remove(user.username());
        users.remove(user.id());
    }

    public Set<AuthToken> tokens() {
        return tokens.values().stream().collect(Collectors.toSet());
    };

    public Collection<Game> games() {
        return games.values().stream().collect(Collectors.toList());
    }

    public Set<User> users() {
        return users.values().stream().collect(Collectors.toSet());
    };

    public HashMap<UUID, AuthToken> tokens;
    public HashMap<String, User> usersByUsername;
    public HashMap<Integer, Game> games;
    public HashMap<Integer, User> users;
}
