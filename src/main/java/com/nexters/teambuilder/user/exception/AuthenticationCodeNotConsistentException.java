package com.nexters.teambuilder.user.exception;

public class AuthenticationCodeNotConsistentException extends RuntimeException {
    public AuthenticationCodeNotConsistentException() {
        super("인증코드가 일치하지 않습니다");
    }
}
