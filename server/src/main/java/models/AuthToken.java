package models;

import java.time.OffsetDateTime;
import java.util.UUID;

public record AuthToken(Integer id, String username, UUID authToken, OffsetDateTime createdAt) implements Model {
    public String getTableName(){
        return "authTokens";
    }
}
