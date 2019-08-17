package com.nexters.teambuilder.session.exception;

public class SessionNotFoundException extends RuntimeException {
    public SessionNotFoundException(Integer sessionNumber) {
        super("session not found by sessionNumber : " + sessionNumber);
    }
}
