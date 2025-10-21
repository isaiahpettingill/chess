package service;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

import dataaccess.InMemoryAuthRepository;
import dataaccess.InMemoryUserRepository;
import models.AuthToken;
import models.User;


public final class AuthService implements Service {
    private final InMemoryAuthRepository authRepository;
    private final InMemoryUserRepository userRepository;

    public AuthService(InMemoryAuthRepository authRepository, InMemoryUserRepository userRepository) {
        this.authRepository = authRepository;
        this.userRepository = userRepository;
    }

    public boolean validToken(UUID id){
        return this.authRepository.exists(x -> x.authToken().equals(id));
    }

    public Optional<User> getUserFromToken(UUID id){
        final var token = this.authRepository.get(id);
        if (token.isPresent()){
            final var username = token.get().username();
            final var user = this.userRepository.getByUsername(username);
            return user;
        }
        return Optional.empty();
    }

    public void logout(UUID id){
        this.authRepository.delete(id);
    }

    public UUID generateToken(){
        return UUID.randomUUID();
    }

    public void saveToken(UUID token, String username){
        this.authRepository.upsert(
            new AuthToken(
                null,
                username,
                token, 
                OffsetDateTime.now()
            )
        );
    }
}
