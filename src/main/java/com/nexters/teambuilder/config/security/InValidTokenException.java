package com.nexters.teambuilder.config.security;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.UNAUTHORIZED)
public class InValidTokenException extends RuntimeException {
    public InValidTokenException(){
        super("유효한 토큰이 아닙니다.");
    }
}
