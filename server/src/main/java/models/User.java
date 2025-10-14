package models;

public record User(
        Long id,
        String username,
        String passwordHash,
        String emailAddress) implements Model {
    public String getTableName() {
        return "Users";
    }
}
