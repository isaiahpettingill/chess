package services;

import dataaccess.AuthRepository;

import java.util.Optional;
import java.util.UUID;
import models.AuthToken;

final class AuthService implements Service {
    private final AuthRepository _authRepository;

    public AuthService(AuthRepository authRepository) {
        _authRepository = authRepository;
    }

    // public Optional<AuthToken> checkToken(UUID id) {
        // _authRepository.getToken(id)
    // }
}
