package com.nexters.teambuilder.idea.domain;

import com.nexters.teambuilder.user.domain.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Member {

    public enum Position{
        DESIGNER, DEVELOPER
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer ideaId;

    private String uuid;

    private String id;

    private String name;

    private Integer nextersNumber;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private User.Position position;

    private boolean hasTeam;

    public static Member of(Member member){
        return new Member(member.getIdeaId(), member.getUuid(),
                member.getId(), member.getName(), member.getNextersNumber(),
                member.getPosition(), member.isHasTeam());
    }

}
