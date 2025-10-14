package models;

import java.util.UUID;

public record AuthToken(Long id, String username, UUID authToken) implements Model {
    public String getTableName(){
        return "AuthTokens";
    }
}
