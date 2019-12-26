package com.nexters.teambuilder.idea.api.dto;

import com.nexters.teambuilder.user.domain.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MemberResponse {

    private String uuid;

    private String id;

    private String name;

    private Integer nextersNumber;

    private User.Position position;

    private boolean hasTeam;

    public static  MemberResponse createMemberFrom(User user) {
        return new MemberResponse(user.getUuid(), user.getId(), user.getName(), user.getNextersNumber(),
                user.getPosition(), user.isHasTeam());
    }

}
