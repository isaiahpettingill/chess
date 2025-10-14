package services;

import dataaccess.AuthRepository;


final class AuthService implements Service {
    private final AuthRepository _authRepository;

    public AuthService(AuthRepository authRepository) {
        _authRepository = authRepository;
    }

}
