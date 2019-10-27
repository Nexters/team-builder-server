package com.nexters.teambuilder.common.exception;

import static com.nexters.teambuilder.common.error.ErrorCode.NOT_PERIOD_OF_IDEA_CHECK;
import static com.nexters.teambuilder.common.error.ErrorCode.NOT_PERIOD_OF_IDEA_COLLECT;
import static com.nexters.teambuilder.common.error.ErrorCode.NOT_PERIOD_OF_IDEA_VOTE;
import static com.nexters.teambuilder.common.error.ErrorCode.NOT_PERIOD_OF_TEAM_BUILDING;
import static com.nexters.teambuilder.common.error.ErrorCode.UNKNOWN_ERROR;

import com.nexters.teambuilder.session.domain.Period;

public class NotValidPeriodException extends RuntimeException {
    public NotValidPeriodException(Period.PeriodType periodType) {
        super(getErrorMessageOf(periodType));
    }

    private static String getErrorMessageOf(Period.PeriodType periodType) {
        switch (periodType) {
            case IDEA_COLLECT:
                return NOT_PERIOD_OF_IDEA_COLLECT.getMessage();

            case IDEA_VOTE:
                return NOT_PERIOD_OF_IDEA_VOTE.getMessage();

            case IDEA_CHECK:
                return NOT_PERIOD_OF_IDEA_CHECK.getMessage();

            case TEAM_BUILDING:
                return NOT_PERIOD_OF_TEAM_BUILDING.getMessage();

                default:
                    return UNKNOWN_ERROR.getMessage();
        }
    }
}
