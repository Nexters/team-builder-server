package com.nexters.teambuilder.user.exception;

public class PasswordNotMatedException extends RuntimeException {
    public PasswordNotMatedException() {
        super("password not matched");
    }
}
