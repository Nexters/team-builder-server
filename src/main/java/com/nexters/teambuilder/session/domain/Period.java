package com.nexters.teambuilder.session.domain;

import static java.time.ZonedDateTime.now;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.Optional;
import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import com.nexters.teambuilder.session.api.dto.PeriodRequest;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.tomcat.jni.Local;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
public class Period {
    public enum PeriodType {
        IDEA_COLLECT, IDEA_VOTE, IDEA_CHECK, TEAM_BUILDING
    }

    @Enumerated(EnumType.STRING)
    private PeriodType periodType;

    private ZonedDateTime startDate;

    private ZonedDateTime endDate;

    public Period(PeriodType periodType, ZonedDateTime startDate, ZonedDateTime endDate) {
        this.periodType = periodType;
        this.startDate = startDate;
        if (!Optional.ofNullable(startDate).isPresent()) {
            this.startDate = now().plusWeeks(this.periodType.ordinal()).plusDays(1);
        }
        this.endDate = endDate;
        if (!Optional.ofNullable(endDate).isPresent()) {
            this.endDate = now().plusWeeks(this.periodType.ordinal() + 1);
        }
    }

    public static Period of (PeriodRequest request) {
        return new Period(request.getPeriodType(), request.getStartDate(), request.getEndDate());
    }

    public boolean isNowIn() {
        LocalDate realEndDate = endDate.toLocalDate();
        return ZonedDateTime.now().isAfter(startDate) && LocalDate.now().isBefore(realEndDate.plusDays(1));
    }
}
