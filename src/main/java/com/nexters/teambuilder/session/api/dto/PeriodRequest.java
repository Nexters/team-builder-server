package com.nexters.teambuilder.session.api.dto;

import java.time.ZonedDateTime;

import com.nexters.teambuilder.session.domaiin.Period;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PeriodRequest {
    private Period.PeriodType periodType;

    private ZonedDateTime startDate;

    private ZonedDateTime endDate;
}
