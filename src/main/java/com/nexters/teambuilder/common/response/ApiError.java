package com.nexters.teambuilder.common.response;

import java.time.ZonedDateTime;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ApiError {
    private int status;
    private int errorCode;
    private String error;
    private String message;
    private ZonedDateTime timestamp;

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
}
