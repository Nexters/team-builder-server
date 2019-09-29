package com.nexters.teambuilder.user.api.dto;

import com.nexters.teambuilder.session.domain.SessionUser;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SessionUserResponse extends UserResponse {

    private boolean voted;

    private boolean submitIdea;

    private boolean hasTeam;

    public SessionUserResponse(SessionUser sessionUser) {
        super(sessionUser.getUser().getUuid(), sessionUser.getUser().getId(), sessionUser.getUser().getName(),
                sessionUser.getUser().getNextersNumber(), sessionUser.getUser().getRole(),
                sessionUser.getUser().getPosition(), sessionUser.getUser().getEmail(),
                sessionUser.getUser().isActivated(), sessionUser.getUser().getCreatedAt());

        this.voted = sessionUser.isVoted();

        this.submitIdea = sessionUser.isSubmitIdea();

        this.hasTeam = isHasTeam();
    }
}
