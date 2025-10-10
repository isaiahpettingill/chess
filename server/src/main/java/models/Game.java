package models;

public record Game(
    long id,
    String gameName,
    String whiteUsername,
    String blackUsername,
    String game
) implements Model {
        public String getTableName(){
        return "Games";
    }
}

