package com.nexters.teambuilder.person.exception;

public class PersonNotFoundException extends RuntimeException {
    public PersonNotFoundException(Integer personId) {
        super("could not found person by id : " + personId);
    }
}
