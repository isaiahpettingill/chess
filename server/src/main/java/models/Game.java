package models;

public record Game(
    long gameId, 
    String gameName,
    String whiteUsername,
    String blackUsername,
    byte[] game
) {
    
}
