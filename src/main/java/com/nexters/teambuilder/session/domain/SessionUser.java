package com.nexters.teambuilder.session.domain;

import com.nexters.teambuilder.user.domain.User;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.util.Assert;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Getter
@NoArgsConstructor
public class SessionUser {
    @Embeddable
    @Getter
    @NoArgsConstructor
    @EqualsAndHashCode
    public static class Id implements Serializable {
        private Integer sessionId;
        private String uuid;

        Id(Integer sessionId, String uuid) {
            this.sessionId = sessionId;
            this.uuid = uuid;
        }
    }

    @EmbeddedId
    private Id id;

    @ManyToOne
    @MapsId("sessionId")
    private Session session;

    @ManyToOne
    @MapsId("uuid")
    private User user;

    private int voteCount;

    private boolean voted;

    private boolean submitIdea;

    private boolean hasTeam;

    public SessionUser(Session session, User user) {
        Assert.notNull(session, "session must not be null");
        Assert.notNull(user, "user must not be null");

        this.id = new Id(session.getSessionId(), user.getUuid());
        this.session = session;
        this.user = user;
    }

    public void plusVoteCount () {
        this.voteCount++;
    }

    public void updateVoted() {
        this.voted = true;
    }

    public void updateSubmitIdea() {
        this.submitIdea = true;
    }

    public void updateHasTeam() {
        this.hasTeam = true;
    }
}
