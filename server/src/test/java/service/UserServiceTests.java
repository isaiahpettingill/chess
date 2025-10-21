package service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import dataaccess.InMemoryUserRepository;
import dto.RegisterPayload;

public class UserServiceTests {
    @Test
    public void userserviceavesUser() {
        final var userService = new UserService(new InMemoryUserRepository());

        userService.saveUser(new RegisterPayload(
                "Bob Jones",
                "Password1!",
                "bob@jones.com"));

        Assertions.assertTrue(userService.isAlreadyTaken("Bob Jones"));
    }

    @Test
    public void userServiceFailsToAddDuplicateUser() {
        final var repo = new InMemoryUserRepository();
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

}
