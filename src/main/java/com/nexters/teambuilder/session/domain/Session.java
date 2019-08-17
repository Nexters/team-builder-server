package com.nexters.teambuilder.session.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javax.persistence.CollectionTable;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;

import com.nexters.teambuilder.session.api.dto.SessionRequest;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Session {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer sessionId;

    private Integer sessionNumber;

    @ElementCollection
    @CollectionTable(name = "session_period", joinColumns = @JoinColumn(name = "sessionId"))
    private List<Period> periods = new ArrayList<>();

    private String logoImageUrl;

    public Session(Integer sessionNumber, List<Period> periods, String logoImageUrl) {
        this.sessionNumber = sessionNumber;
        this.periods = periods;
        this.logoImageUrl = logoImageUrl;
    }

    public void update(SessionRequest request) {
        this.sessionNumber = request.getSessionNumber();
        this.periods = request.getPeriods().stream().map(Period::of).collect(Collectors.toList());
        this.logoImageUrl = request.getLogoImageUrl();
    }

    public static Session of(SessionRequest sessionRequest) {
        List<Period> periods = sessionRequest.getPeriods().stream().map(Period::of).collect(Collectors.toList());

        return new Session(sessionRequest.getSessionNumber(), periods, sessionRequest.getLogoImageUrl());
    }
}


