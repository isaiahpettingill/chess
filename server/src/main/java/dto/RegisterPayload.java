package dto;

public final record RegisterPayload(
    String username, String password, String email
) implements ValidatedPayload {
    @Override
    public boolean valid() {
        return username != null
                && password != null
                && email != null
                && !username.isBlank()
                && !password.isBlank()
                && !email.isBlank();
    }
}
