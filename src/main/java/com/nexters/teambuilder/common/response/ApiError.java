package com.nexters.teambuilder.common.response;

import com.nexters.teambuilder.idea.api.dto.MemberResponse;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.time.ZonedDateTime;
import java.util.List;

@Getter
public class ApiError {
    private int status;
    private int errorCode;
    private String error;
    private String message;
    private ZonedDateTime timestamp;
    private List<MemberResponse> hasTeamMembers;

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

    public ApiError(HttpStatus status, Integer errorCode, String message, List<MemberResponse> hasTeamMembers) {
        this.status = status.value();
        this.errorCode = errorCode;
        this.error = status.getReasonPhrase();
        this.message = message;
        this.hasTeamMembers = hasTeamMembers;
        this.timestamp = ZonedDateTime.now();
    }
}
