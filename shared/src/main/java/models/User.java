package models;

public record User(
        Integer id,
        String username,
        String passwordHash,
        String emailAddress) implements Model {
    public String getTableName() {
        return "users";
    }
}
