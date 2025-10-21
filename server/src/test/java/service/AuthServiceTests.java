package service;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.UUID;

import org.junit.jupiter.api.*;

import dataaccess.*;
import models.User;


public class AuthServiceTests {


    @Test
    public void canAddAuthToken() {
        final InMemoryAuthRepository authRepository = new InMemoryAuthRepository();
        final AuthService authService = new AuthService(authRepository, new InMemoryUserRepository());

        var token = UUID.randomUUID();
        authService.saveToken(token, "your mom");
        Assertions.assertTrue(authService.validToken(token));
    }


    @Test
    public void cannotAddSameAuthTokenTwice() {
        final InMemoryAuthRepository authRepository = new InMemoryAuthRepository();
        final AuthService authService = new AuthService(authRepository, new InMemoryUserRepository());

        var token = UUID.randomUUID();
        authService.saveToken(token, "your mom");
        authService.saveToken(token, "your mom");

        Assertions.assertEquals(authRepository.list().stream().count(), 1);
    }

    @Test
    public void getUserFromTokenWorks(){
        var db = new InMemoryDatabase();
        db.users().add(new User(100, "bob", "asdf", "bob@jones.com"));
        final InMemoryAuthRepository authRepository = new InMemoryAuthRepository();
        final AuthService authService = new AuthService(authRepository, new InMemoryUserRepository());

        final var token = UUID.randomUUID();
        authService.saveToken(token, "bob");
        var user = authService.getUserFromToken(token);
        assertTrue(user.isPresent());
        assertTrue(user.get().username() == "bob");
    }
    
}
