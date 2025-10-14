package dto;

public final record RegisterPayload(
    String username, String password, String email
) {
    
}
