package dto;

import java.util.Collection;

public final record ListGamesResponse(Collection<ListGamesGame> games) {
    public final record ListGamesGame(Integer gameID, String whiteUsername, String blackUsername, String gameName){}
}
