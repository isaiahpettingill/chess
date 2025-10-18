package services;

import java.util.UUID;

import dataaccess.AuthRepository;


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
}
