package com.nexters.teambuilder.common.exception;

public class CommonNotFoundException extends RuntimeException {
    public CommonNotFoundException(Integer id) {
        super("common not found by id : " + id);
    }

    public CommonNotFoundException() {
        super("common not found");
    }
}
