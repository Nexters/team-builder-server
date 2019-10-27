package com.nexters.teambuilder.common.error;

import lombok.Getter;

public enum ErrorCode {
    UNKNOWN_ERROR("error.unknown", 90000),

    NOT_PERIOD_OF_IDEA_COLLECT("error.not.period.of.idea.collect", 90001),
    NOT_PERIOD_OF_IDEA_VOTE("error.not.period.of.idea.vote", 90002),
    NOT_PERIOD_OF_IDEA_CHECK("error.not.period.of.idea.check", 90003),
    NOT_PERIOD_OF_TEAM_BUILDING("error.not.period.of.team.building", 90004);

    @Getter
    private String message;
    private Integer code;

    ErrorCode(String message, Integer code) {
        this.message = message;
        this.code = code;
    }

    public static Integer getCodeOf(String message) {
        return of(message).code;
    }

    public static ErrorCode of(String message) {
        for (ErrorCode v : ErrorCode.values()) {
            if (v.message.equalsIgnoreCase(message)) {
                return v;
            }
        }

        return UNKNOWN_ERROR;
    }
}

