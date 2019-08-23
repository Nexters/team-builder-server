package com.nexters.teambuilder.session.api.dto;

import static java.time.ZonedDateTime.now;

import java.time.ZonedDateTime;

import com.nexters.teambuilder.session.domain.Period;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PeriodResponse {
    private Period.PeriodType periodType;

    private ZonedDateTime startDate;

    private ZonedDateTime endDate;

    private boolean now;

    public PeriodResponse(Period.PeriodType periodType, ZonedDateTime startDate, ZonedDateTime endDate) {
        this.periodType = periodType;
        this.startDate = startDate;
        this.endDate = endDate;
        ZonedDateTime now = now();
        if(!periodType.equals(Period.PeriodType.TEAM_BUILDING)) {
            this.now = startDate.isBefore(now) && endDate.isAfter(now);
        }
    }

    public static PeriodResponse of(Period period) {
        return new PeriodResponse(period.getPeriodType(), period.getStartDate(), period.getEndDate());
    }
}
