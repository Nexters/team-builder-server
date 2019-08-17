package com.nexters.teambuilder.session.domain;

import java.time.ZonedDateTime;
import javax.persistence.Embeddable;

import com.nexters.teambuilder.session.api.dto.PeriodRequest;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Embeddable
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Period {
    public enum PeriodType {
        IDEA_COLLECT, IDEA_VOTE, IDEA_CHECK, TEAM_BUILDING
    }

    private PeriodType periodType;

    private ZonedDateTime startDate;

    private ZonedDateTime endDate;

    public static Period of (PeriodRequest request) {
        return new Period(request.getPeriodType(), request.getStartDate(), request.getEndDate());
    }
}
