package service;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.UUID;

import org.junit.jupiter.api.*;

import dataaccess.*;
import models.User;

@TestMethodOrder(MethodOrderer.DisplayName.class)
public class AuthServiceTests {

    @Test
    public void canAddAuthToken() {
        final var db = new InMemoryDatabase();
        final InMemoryAuthRepository authRepository = new InMemoryAuthRepository(db);
        final AuthService authService = new AuthService(authRepository, new InMemoryUserRepository(db));

        var token = UUID.randomUUID();
        authService.saveToken(token, "your mom");
        Assertions.assertTrue(authService.validToken(token));
    }

    @Test
    public void logoutWorks(){
        final var db = new InMemoryDatabase();
        final InMemoryAuthRepository authRepository = new InMemoryAuthRepository(db);
        final AuthService authService = new AuthService(authRepository, new InMemoryUserRepository(db));

        var token = UUID.randomUUID();
        authService.saveToken(token, "your mom");
        authService.logout(token);
        assertFalse(authRepository.get(token).isPresent());
    }

    
    @Test
    public void logoutSwallowsDuplicates(){
        final var db = new InMemoryDatabase();
        final InMemoryAuthRepository authRepository = new InMemoryAuthRepository(db);
        final AuthService authService = new AuthService(authRepository, new InMemoryUserRepository(db));

        var token = UUID.randomUUID();
        var token2 = UUID.randomUUID();
        authService.saveToken(token, "your mom");
        authService.saveToken(token2, "stacy's mom");
        for (int i = 0; i < 1000; i++){
            authService.logout(token);
        }
        assertFalse(authRepository.get(token).isPresent());
        assertTrue(authRepository.get(token2).isPresent());
    }


    @Test
    public void cannotAddSameAuthTokenTwice() {
        final var db = new InMemoryDatabase();
        final InMemoryAuthRepository authRepository = new InMemoryAuthRepository(db);
        final AuthService authService = new AuthService(authRepository, new InMemoryUserRepository(db));

        var token = UUID.randomUUID();
        authService.saveToken(token, "your mom");
        authService.saveToken(token, "your mom");

        Assertions.assertEquals(1, authRepository.list().size());
    }

    @Test
    public void getUserFromTokenWorks(){
        var db = new InMemoryDatabase();
        db.addUser(new User(100, "bob", "asdf", "bob@jones.com"));
        final InMemoryAuthRepository authRepository = new InMemoryAuthRepository(db);
        final AuthService authService = new AuthService(authRepository, new InMemoryUserRepository(db));

        final var token = UUID.randomUUID();
        authService.saveToken(token, "bob");
        var user = authService.getUserFromToken(token);
        assertTrue(user.isPresent());
        assertTrue(user.get().username().equals("bob"));
    }
    
    @Test
    public void getUserFromTokenDoesntCreateFakeUsers(){
        var db = new InMemoryDatabase();
        db.users().add(new User(100, "bob", "asdf", "bob@jones.com"));
        final InMemoryAuthRepository authRepository = new InMemoryAuthRepository(db);
        final AuthService authService = new AuthService(authRepository, new InMemoryUserRepository(db));

        final var token = UUID.randomUUID();
        authService.saveToken(token, "bob");

        var user = authService.getUserFromToken(UUID.randomUUID());
        assertFalse(user.isPresent());
    }

    @Test
    public void validTokenIsValid(){
        var db = new InMemoryDatabase();
        db.users().add(new User(100, "bob", "asdf", "bob@jones.com"));
        final InMemoryAuthRepository authRepository = new InMemoryAuthRepository(db);
        final AuthService authService = new AuthService(authRepository, new InMemoryUserRepository(db));

        final var token = UUID.randomUUID();
        authService.saveToken(token, "bob");

        assertTrue(authService.validToken(token));
    }

    @Test
    public void invalidTokenInvalid(){
        var db = new InMemoryDatabase();
        db.users().add(new User(100, "bob", "asdf", "bob@jones.com"));
        final InMemoryAuthRepository authRepository = new InMemoryAuthRepository(db);
        final AuthService authService = new AuthService(authRepository, new InMemoryUserRepository(db));

        final var token = UUID.randomUUID();
        authService.saveToken(token, "bob");

        assertFalse(authService.validToken(UUID.randomUUID()));
    }
}
