package models;

public record User(
        long id,
        String username,
        String passwordHash,
        String emailAddress) implements Model {
    public String getTableName() {
        return "Users";
    }
}
