package com.nexters.teambuilder.user.exception;

public class UserNotActivatedException extends RuntimeException {
    public UserNotActivatedException() {
        super("user is not activated.");
    }
}

