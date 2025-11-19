package com.chico.chico.exception;

public class InvalidPasswordOrEmailException extends RuntimeException {
    public InvalidPasswordOrEmailException(String message) {
        super(message);
    }
}
