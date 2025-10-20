package dto;

import java.util.Collection;

import models.Game;

public final record ListGamesResponse(Collection<Game> games) {
    
}
