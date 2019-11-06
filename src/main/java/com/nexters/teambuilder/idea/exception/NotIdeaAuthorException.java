package com.nexters.teambuilder.idea.exception;

import static com.nexters.teambuilder.common.error.ErrorCode.NOT_AUTHOR_OF_IDEA;

public class NotIdeaAuthorException extends RuntimeException {
    public NotIdeaAuthorException() {
        super(NOT_AUTHOR_OF_IDEA.getMessage());
    }
}
