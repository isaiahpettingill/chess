package models;

public record User(
    long userId,
    String username,
    String passwordHash,
    String emailAddress
) {
    
}
