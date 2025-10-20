package handlers;

import java.util.Optional;
import java.util.UUID;

import io.javalin.http.Context;
import services.AuthService;

public abstract class AuthorizedHandler {
    private final String AUTHORIZATION = "Authorization";
    protected final AuthService _authService;

    protected AuthorizedHandler(AuthService authService) {
        _authService = authService;
    }

    protected Optional<UUID> authToken(Context context) {
        try {
            final var tokenHeader = context.header(AUTHORIZATION);
            if (tokenHeader == null) return Optional.empty();
            return Optional.of(UUID.fromString(tokenHeader));
        } catch (IllegalArgumentException __) {
            return Optional.empty();
        }
    };
    
    protected boolean authorize(Context context) {
        final var auth = authToken(context);
        if (auth == null || !auth.isPresent() || !_authService.validToken(auth.get())) {
            context.status(401);
            context.result(HttpErrors.UNAUTHORIZED);
            return false;
        }
        return true;
    }
}
