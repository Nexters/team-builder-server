package com.nexters.teambuilder.session.exception;

public class SessionNotFoundException extends RuntimeException {
    public SessionNotFoundException(Integer sessionId) {
        super("session not found by id : " + sessionId);
    }
}
