package com.nexters.teambuilder.global.exception;

import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.nexters.teambuilder.common.exception.ActionForbiddenException;
import com.nexters.teambuilder.common.exception.CommonNotFoundException;
import com.nexters.teambuilder.common.response.ApiError;
import com.nexters.teambuilder.favorite.exception.FavoriteNotFoundException;
import com.nexters.teambuilder.person.exception.PersonNotFoundException;
import com.nexters.teambuilder.session.exception.SessionNotFoundException;
import com.nexters.teambuilder.tag.exception.TagNotFoundException;
import com.nexters.teambuilder.user.exception.LoginErrorException;
import com.nexters.teambuilder.user.exception.UserNotActivatedException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
@ResponseBody
public class GlobalControllerExceptionHandler {
    /**
     * exception handler for episode, creator, title not found exception.
     * @param ex RuntimeException
     * @return Api Error Wrapper
     */
    @ExceptionHandler(value = {
            PersonNotFoundException.class,
            LoginErrorException.class,
            TagNotFoundException.class,
            SessionNotFoundException.class,
            CommonNotFoundException.class,
            FavoriteNotFoundException.class
    })
    @ResponseStatus(HttpStatus.NOT_FOUND)
    protected ApiError handleNotFound(RuntimeException ex) {
        return new ApiError(HttpStatus.NOT_FOUND, 0, ex.getMessage());
    }

    @ExceptionHandler(value = {
            ActionForbiddenException.class,
            UserNotActivatedException.class
    })
    @ResponseStatus(HttpStatus.FORBIDDEN)
    protected ApiError Forbidden(RuntimeException ex) {
        return new ApiError(HttpStatus.FORBIDDEN, 0, ex.getMessage());
    }

    /**
     * exception handler for InvalidFormat and ConstraintViolation exceptions.
     * @param ex RuntimeException
     * @return Api Error Wrapper
     */
    @ExceptionHandler(value = {
            InvalidFormatException.class,
            IllegalArgumentException.class,
    })
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    protected ApiError handleBadRequest(RuntimeException ex) {
        return new ApiError(HttpStatus.BAD_REQUEST, 0, ex.getMessage());
    }

    /**
     * exception handler for invalid request parameters exceptions.
     * @param ex MethodArgumentNotValidException
     * @return Api Error Wrapper
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleInvalidParam(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(fieldError -> fieldError.getField() + ": " + fieldError.getDefaultMessage() + ".")
                .collect(Collectors.joining("\n"));
        return new ApiError(HttpStatus.BAD_REQUEST, 0, message);
    }
}
