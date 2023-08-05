package com.lms.lms.member.exception;

public class MemberNotEmailAuthException extends RuntimeException {

    public MemberNotEmailAuthException(String error) {
        super(error);
    }
}
