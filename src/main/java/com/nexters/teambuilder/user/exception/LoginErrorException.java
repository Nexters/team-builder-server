package com.nexters.teambuilder.user.exception;

public class LoginErrorException extends RuntimeException {
    public LoginErrorException(String id) {
        super("not exist id : " + id);
    }
}
