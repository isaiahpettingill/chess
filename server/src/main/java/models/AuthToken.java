package models;

import java.util.UUID;

public record AuthToken(
    String username,
    UUID authToken
) {
    
}
