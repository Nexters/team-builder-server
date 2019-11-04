package com.nexters.teambuilder.idea.domain;

import com.nexters.teambuilder.user.domain.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Embeddable;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Member {
    private String uuid;

    private String id;

    private String name;

    private Integer nextersNumber;

    private User.Position position;

    private boolean hasTeam;
}
