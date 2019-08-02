package com.nexters.teambuilder.common.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BaseResponse<T> {
    private int httpStatus;
    private String message;
    private int responseCode;
    private T result;
}