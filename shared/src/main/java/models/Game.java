package models;

public record Game(
        Integer id,
        String gameName,
        String whiteUsername,
        String blackUsername,
        String game,
        boolean isOver) implements Model {
    public String getTableName() {
        return "games";
    }

    public Game(Integer id,
            String gameName,
            String whiteUsername,
            String blackUsername,
            String game) {
        this(id, gameName, whiteUsername, blackUsername, game, false);
    }
}
