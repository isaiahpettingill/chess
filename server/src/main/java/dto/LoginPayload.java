package dto;

public final record LoginPayload(String username, String password) implements ValidatedPayload {

    @Override
    public boolean valid() {
        return username != null
                && password != null
                && !username.isBlank()
                && !password.isBlank();
    }

}
