package com.nexters.teambuilder.idea.exception;

public class NotHasRightVoteException extends RuntimeException {
    public NotHasRightVoteException() {
        super("해당 기수에 참여하지 않는 사용자입니다.");
    }
}
