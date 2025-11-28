package com.chico.chico.exception;

public class UserIsNotATeacherException extends RuntimeException {
    public UserIsNotATeacherException(String message) {
        super(message);
    }
}
