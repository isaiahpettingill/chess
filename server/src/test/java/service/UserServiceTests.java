package service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import dataaccess.InMemoryDatabase;
import dataaccess.InMemoryUserRepository;
import dto.RegisterPayload;

public class UserServiceTests {
    @Test
    public void userserviceavesUser() {
        final var db = new InMemoryDatabase();

        final var userService = new UserService(new InMemoryUserRepository(db));

        userService.saveUser(new RegisterPayload(
                "Bob Jones",
                "Password1!",
                "bob@jones.com"));

        Assertions.assertTrue(userService.isAlreadyTaken("Bob Jones"));
    }

    @Test
    public void userServiceFailsToAddDuplicateUser() {
        final var db = new InMemoryDatabase();

        final var repo = new InMemoryUserRepository(db);
        final var userService = new UserService(repo);

        userService.saveUser(new RegisterPayload(
                "Bob Jones",
                "Password1!",
                "bob@jones.com"));

        userService.saveUser(new RegisterPayload(
                "Bob Jones",
                "Password1!",
                "bob@jones.com"));

        userService.saveUser(new RegisterPayload(
                "Bob Jones",
                "Password1!",
                "bob@jones.com"));

        assertEquals(repo.list().stream().count(), 1);
    }

    @Test
    public void validLoginWorks(){
        final var db = new InMemoryDatabase();

        final var repo = new InMemoryUserRepository(db);
        final var userService = new UserService(repo);

        final var user = userService.saveUser(new RegisterPayload(
                "Bob Jones",
                "Password1!",
                "bob@jones.com"));

        assertNotEquals(user.get().passwordHash(), "Password1!");
        assertTrue(userService.validLogin("Bob Jones", "Password1!"));
    }

    @Test
    public void invalidLoginDoesNotWork(){
        final var db = new InMemoryDatabase();

        final var repo = new InMemoryUserRepository(db);
        final var userService = new UserService(repo);

        userService.saveUser(new RegisterPayload(
                "Bob Jones",
                "Password1!",
                "bob@jones.com"));

        assertFalse(userService.validLogin("Bob Jones", "Password2!"));
        assertFalse(userService.validLogin("BobJones", "Password1!"));
    }

    @Test
    public void isAlreadyTakenDoesNotTriggerWhenValid(){
        final var db = new InMemoryDatabase();

        final var repo = new InMemoryUserRepository(db);
        final var userService = new UserService(repo);

        userService.saveUser(new RegisterPayload(
                "Bob Jones",
                "Password1!",
                "bob@jones.com"));
        
        assertFalse(userService.isAlreadyTaken("bob jones"));
    }

    @Test
    public void isAlreadyTakenWorks(){
        final var db = new InMemoryDatabase();

        final var repo = new InMemoryUserRepository(db);
        final var userService = new UserService(repo);

        userService.saveUser(new RegisterPayload(
                "Bob Jones",
                "Password1!",
                "bob@jones.com"));
        
        assertTrue(userService.isAlreadyTaken("Bob Jones"));
    }

}
