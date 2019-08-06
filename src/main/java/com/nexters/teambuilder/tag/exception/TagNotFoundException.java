package com.nexters.teambuilder.tag.exception;

public class TagNotFoundException extends RuntimeException {
    public TagNotFoundException(Integer tagId) {
        super("tag could not found by id : " + tagId);
    }
}
