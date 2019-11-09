package com.nexters.teambuilder.idea.exception;

import static com.nexters.teambuilder.common.error.ErrorCode.USER_FORBIDDEN;

public class UserForbiddenActionException extends RuntimeException {
    public UserForbiddenActionException() {
        super(USER_FORBIDDEN.getMessage());
    }
}
