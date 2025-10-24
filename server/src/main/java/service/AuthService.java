package service;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

import dataaccess.Repository;
import dataaccess.inmemory.InMemoryAuthRepository;
import dataaccess.inmemory.InMemoryUserRepository;

import java.util.Random;
import models.AuthToken;
import models.User;


public final class AuthService implements Service {
    private final Repository<AuthToken, UUID> authRepository;
    private final Repository<User, Integer> userRepository;

    public AuthService(Repository<AuthToken, UUID> authRepository, Repository<User, Integer> userRepository) {
        this.authRepository = authRepository;
        this.userRepository = userRepository;
    }

    public boolean validToken(UUID id){
        return this.authRepository.exists(x -> x.authToken().equals(id));
    }

    private Optional<User> getByUsername(String username) {
        return userRepository.getBy(x -> x.username().equals(username));
    }

    public Optional<User> getUserFromToken(UUID id){
        final var token = this.authRepository.get(id);
        if (token.isPresent()){
            final var username = token.get().username();
            final var user = getByUsername(username);
            return user;
        }
        return Optional.empty();
    }

    public void logout(UUID id){
        this.authRepository.delete(id);
    }

    public void saveToken(UUID token, String username){
        this.authRepository.upsert(
            new AuthToken(
                new Random().nextInt(0, Integer.MAX_VALUE),
                username,
                token, 
                OffsetDateTime.now()
            )
        );
    }
}
