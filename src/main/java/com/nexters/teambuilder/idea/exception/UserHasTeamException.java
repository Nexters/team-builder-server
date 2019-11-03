package com.nexters.teambuilder.idea.exception;

import static com.nexters.teambuilder.common.error.ErrorCode.USER_HAS_TEAM;

public class UserHasTeamException extends RuntimeException {
    public UserHasTeamException() {
        super(USER_HAS_TEAM.getMessage());
    }
}