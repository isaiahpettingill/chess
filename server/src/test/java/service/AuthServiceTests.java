package service;

import org.junit.jupiter.api.*;

import dataAccess.*;


public class AuthServiceTests {
    @Test
    public void canAddAuthToken() {
        final InMemoryAuthRepository authRepository = new InMemoryAuthRepository();
        final AuthService authService = new AuthService(authRepository, new InMemoryUserRepository());

        var token = authService.generateToken();
        authService.saveToken(token, "your mom");
        Assertions.assertTrue(authService.validToken(token));
    }


    @Test
    public void cannotAddSameAuthTokenTwice() {
        final InMemoryAuthRepository authRepository = new InMemoryAuthRepository();
        final AuthService authService = new AuthService(authRepository, new InMemoryUserRepository());

        var token = authService.generateToken();
        authService.saveToken(token, "your mom");
        authService.saveToken(token, "your mom");

        Assertions.assertEquals(authRepository.list().stream().count(), 1);
    }
    
}
