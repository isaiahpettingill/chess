package services;

import java.time.OffsetDateTime;
import java.util.UUID;

import dataaccess.AuthRepository;
import models.AuthToken;


public final class AuthService implements Service {
    private final AuthRepository _authRepository;

    public AuthService(AuthRepository authRepository) {
        _authRepository = authRepository;
    }

    public boolean validToken(UUID id){
        return _authRepository.exists(x -> x.authToken().equals(id));
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
