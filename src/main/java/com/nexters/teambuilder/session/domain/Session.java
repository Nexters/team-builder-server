package com.nexters.teambuilder.session.domain;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import com.nexters.teambuilder.session.api.dto.SessionRequest;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(uniqueConstraints =
    @UniqueConstraint(columnNames = "sessionNumber"))
@Getter
@Setter
@NoArgsConstructor
public class Session {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer sessionId;

    private Integer sessionNumber;

    private boolean teamBuildingMode;

    @ElementCollection
    @CollectionTable(name = "session_period", joinColumns = @JoinColumn(name = "sessionId"))
    private List<Period> periods = new ArrayList<>();

    private String logoImageUrl;

    private Integer maxVoteCount;

    @OneToMany(mappedBy = "session", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<SessionUser> sessionUsers = new HashSet<>();

    public Session(Integer sessionNumber, boolean teamBuildingMode, List<Period> periods,
                   String logoImageUrl, Integer maxVoteCount) {
        this.sessionNumber = sessionNumber;
        this.teamBuildingMode = teamBuildingMode;
        this.periods = periods;
        this.logoImageUrl = logoImageUrl;
        this.maxVoteCount = maxVoteCount;
    }

    public void update(SessionRequest request) {
        this.teamBuildingMode = request.isTeamBuildingMode();
        this.periods = request.getPeriods().stream().map(Period::of).collect(Collectors.toList());
        this.logoImageUrl = request.getLogoImageUrl();
    }

    public static Session of(Integer sessionNumber, SessionRequest sessionRequest) {
        List<Period> periods = sessionRequest.getPeriods().stream().map(Period::of).collect(Collectors.toList());

        return new Session(sessionNumber, sessionRequest.isTeamBuildingMode(), periods,
                sessionRequest.getLogoImageUrl(), sessionRequest.getMaxVoteCount());
    }
}


