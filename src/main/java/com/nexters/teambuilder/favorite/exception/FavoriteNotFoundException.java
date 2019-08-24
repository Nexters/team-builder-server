package com.nexters.teambuilder.favorite.exception;

public class FavoriteNotFoundException extends RuntimeException {
    public FavoriteNotFoundException(Integer ideaId) {
        super("could not find favorite idea by idea id " + ideaId);
    }
}
