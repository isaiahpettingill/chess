package models;

import java.util.UUID;

public record AuthToken(Integer id, String username, UUID authToken) implements Model {
    public String getTableName(){
        return "authTokens";
    }
}
