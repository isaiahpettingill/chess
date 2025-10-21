package services;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

import dataaccess.InMemoryAuthRepository;
import dataaccess.InMemoryUserRepository;
import models.AuthToken;
import models.User;


public final class AuthService implements Service {
    private final InMemoryAuthRepository _authRepository;
    private final InMemoryUserRepository _userRepository;

    public AuthService(InMemoryAuthRepository authRepository, InMemoryUserRepository userRepository) {
        _authRepository = authRepository;
        _userRepository = userRepository;
    }

    public boolean validToken(UUID id){
        return _authRepository.exists(x -> x.authToken().equals(id));
    }

    public Optional<User> getUserFromToken(UUID id){
        final var token = _authRepository.get(id);
        if (token.isPresent()){
            final var username = token.get().username();
            final var user = _userRepository.getByUsername(username);
            return user;
        }
        return Optional.empty();
    }

    public void logout(UUID id){
        _authRepository.delete(id);
    }

    public UUID generateToken(){
        return UUID.randomUUID();
    }

    public void saveToken(UUID token, String username){
        _authRepository.upsert(
            new AuthToken(
                null,
                username,
                token, 
                OffsetDateTime.now()
            )
        );
    }
}
