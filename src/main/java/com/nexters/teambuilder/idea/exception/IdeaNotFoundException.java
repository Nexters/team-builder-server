package com.nexters.teambuilder.idea.exception;

public class IdeaNotFoundException extends RuntimeException{
    public IdeaNotFoundException(Integer id) {
        super("could not find idea list by id: " + id);
    }
}
