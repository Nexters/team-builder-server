package com.nexters.teambuilder.common.exception;

public class ActionForbiddenException extends RuntimeException {
    public ActionForbiddenException() {
        super("user not privileged at this action");
    }
}
