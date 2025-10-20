package models;

public record Game(
        Integer id,
        String gameName,
        String whiteUsername,
        String blackUsername,
        String game) implements Model {
    public String getTableName() {
        return "Games";
    }
}
