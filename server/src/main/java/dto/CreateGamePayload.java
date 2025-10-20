package dto;

public final record CreateGamePayload(String gameName) implements ValidatedPayload{

    @Override
    public boolean valid() {
       return gameName != null;
    }
    
}
