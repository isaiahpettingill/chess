package handlers;

import java.util.Optional;
import java.util.UUID;

import io.javalin.http.Context;
import service.AuthService;

public abstract class AuthorizedHandler {
    private final String authorization = "Authorization";
    protected final AuthService authService;

    protected AuthorizedHandler(AuthService authService) {
        this.authService = authService;
    }

    protected Optional<UUID> authToken(Context context) {
        try {
            final var tokenHeader = context.header(authorization);
            if (tokenHeader == null) {
                return Optional.empty();
            }
            return Optional.of(UUID.fromString(tokenHeader));
        } catch (IllegalArgumentException __) {
            return Optional.empty();
        }
    };
    
    protected boolean authorize(Context context) {
        final var auth = authToken(context);
        if (auth == null || !auth.isPresent() || !this.authService.validToken(auth.get())) {
            context.status(401);
            context.result(HttpErrors.UNAUTHORIZED);
            return false;
        }
        return true;
    }
}
