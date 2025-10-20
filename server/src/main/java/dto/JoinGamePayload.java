package dto;

public final record JoinGamePayload(String playerColor, long gameID) implements ValidatedPayload {
    public static final String WHITE = "WHITE";
    public static final String BLACK = "BLACK";
    @Override
    public boolean valid() {
       return playerColor == WHITE || playerColor == BLACK;
    }

    
}
