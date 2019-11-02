package com.nexters.teambuilder.idea.exception;

public class UserHasTeamException extends RuntimeException {
    public UserHasTeamException() {
        super("해당 유저는 소속 팀이 존재합니다.");
    }
}