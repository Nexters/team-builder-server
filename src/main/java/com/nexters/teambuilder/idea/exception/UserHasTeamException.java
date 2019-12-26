package com.nexters.teambuilder.idea.exception;

import com.nexters.teambuilder.user.domain.User;
import lombok.Getter;

import java.util.List;
import static com.nexters.teambuilder.common.error.ErrorCode.USER_HAS_TEAM;

public class UserHasTeamException extends RuntimeException {

    @Getter
    private List<User> hasTeamMembers;

    public UserHasTeamException(List<User> hasTeamMembers) {
        super(USER_HAS_TEAM.getMessage());
        this.hasTeamMembers = hasTeamMembers;
    }
}