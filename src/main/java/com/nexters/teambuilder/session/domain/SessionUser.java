package com.nexters.teambuilder.session.domain;

import java.io.Serializable;
import javax.persistence.Embeddable;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;

import com.nexters.teambuilder.user.domain.User;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.util.Assert;

@Entity
@Getter
@NoArgsConstructor
public class SessionUser {
    @Embeddable
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
}
