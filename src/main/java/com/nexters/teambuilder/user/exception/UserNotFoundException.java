package com.nexters.teambuilder.user.exception;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(String id) {
        super("user not found by id : " + id);
    }
}