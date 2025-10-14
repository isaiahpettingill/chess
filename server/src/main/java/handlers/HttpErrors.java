package handlers;

final class HttpErrors {
    public static final String BAD_REQUEST = "{ \"message\": \"Error: bad request\" }";
    public static final String ALREADY_TAKEN = "{ \"message\": \"Error: already taken\" }";
    public static final String UNAUTHORIZED = "{ \"message\": \"Error: unauthorized\" }";

    public static String createErrorMessage(String message) {
        return "{ \"message\": \"Error: " +  message + "\" }";
    }

}
