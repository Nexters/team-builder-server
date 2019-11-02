package com.nexters.teambuilder.idea.api.dto;

import com.nexters.teambuilder.idea.domain.Member;
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
    private Integer ideaId;

    private String uuid;

    private String id;

    private String name;

    private Integer nextersNumber;

    private User.Position position;

    private boolean hasTeam;

    public static MemberResponse of(Member member){
        return new MemberResponse(member.getIdeaId(), member.getUuid(),
                member.getId(), member.getName(), member.getNextersNumber(),
                member.getPosition(), member.isHasTeam());
    }
}
