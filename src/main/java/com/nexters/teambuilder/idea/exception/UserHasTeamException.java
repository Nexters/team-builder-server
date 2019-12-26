package com.nexters.teambuilder.idea.exception;

import com.nexters.teambuilder.idea.api.dto.MemberResponse;
import lombok.Getter;

import java.util.List;

import static com.nexters.teambuilder.common.error.ErrorCode.USER_HAS_TEAM;

public class UserHasTeamException extends RuntimeException {

    @Getter
    private List<MemberResponse> hasTeamMembers;

    public UserHasTeamException(List<MemberResponse> hasTeamMembers) {
        super(USER_HAS_TEAM.getMessage());
        this.hasTeamMembers = hasTeamMembers;
    }
}