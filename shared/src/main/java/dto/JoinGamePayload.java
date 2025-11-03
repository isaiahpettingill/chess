package dto;

public final record JoinGamePayload(String playerColor, Integer gameID) implements ValidatedPayload {
    public static final String WHITE = "WHITE";
    public static final String BLACK = "BLACK";
    @Override
    public boolean valid() {
       return gameID != null && WHITE.equals(playerColor) || BLACK.equals(playerColor);
    }

    
}
