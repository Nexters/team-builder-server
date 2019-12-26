package com.nexters.teambuilder.common.response;

import java.time.ZonedDateTime;
import java.util.List;

import com.nexters.teambuilder.user.domain.User;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ApiError {
    private int status;
    private int errorCode;
    private String error;
    private String message;
    private ZonedDateTime timestamp;
    private List<User> hasTeamMembers;

    /**
     * Api Error Response Wrapper.
     * @param status Http Status
     * @param message Error message
     */
    public ApiError(HttpStatus status, Integer errorCode, String message) {
        this.status = status.value();
        this.errorCode = errorCode;
        this.error = status.getReasonPhrase();
        this.message = message;
        this.timestamp = ZonedDateTime.now();
    }

    public ApiError(HttpStatus status, Integer errorCode, String message, List<User> hasTeamMembers) {
        this.status = status.value();
        this.errorCode = errorCode;
        this.error = status.getReasonPhrase();
        this.message = message;
        this.hasTeamMembers = hasTeamMembers;
        this.timestamp = ZonedDateTime.now();
    }
}
