package models;

import java.util.UUID;

public record AuthToken(long id, String username, UUID authToken) implements Model {
    public String getTableName(){
        return "AuthTokens";
    }
}
