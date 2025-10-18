package dataaccess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import models.*;

public class InMemoryDatabase {
    public InMemoryDatabase() {
        if (tokens == null)
            tokens = new HashSet<>();
        if (games == null)
            games = new ArrayList<>();
        if (users == null)
            users = new HashSet<>();
    }

    public void addToken(AuthToken token) {
        tokens.add(token);
    }

    public void deleteToken(AuthToken token) {
        tokens.remove(token);
    }

    public Optional<AuthToken> getToken(UUID token) {
        if (token == null)
            return Optional.empty();
        return tokens
                .stream()
                .filter(x -> x.authToken().equals(token))
                .findFirst();
    }

    public void addGame(Game game) {
        games.add(game);
    }

    public void deleteGame(Game game) {
        games.remove(game);
    }

    public Optional<Game> getGame(Long gameId) {
        if (gameId == null)
            return Optional.empty();
        return games.stream().filter(x -> x.id().equals(gameId)).findFirst();
    }

    public Optional<User> getUser(Long userId) {
        if (userId == null)
            return Optional.empty();
        return users.stream().filter(x -> x.id().equals(userId)).findFirst();
    }

    public void addUser(User user) {
        users.add(user);
    }

    public void deleteUser(User user) {
        users.remove(user);
    }

    public Set<AuthToken> tokens() {
        return tokens;
    };

    public Collection<Game> games() {
        return games;
    }

    public Set<User> users() {
        return users;
    };

    private static Set<AuthToken> tokens;
    private static Collection<Game> games;
    private static Set<User> users;
}
