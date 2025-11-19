package com.chico.chico.exception;

import com.chico.chico.response.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUserNotFound(UserNotFoundException exception) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse(exception.getMessage(), HttpStatus.NOT_FOUND.value()));
    }

    @ExceptionHandler(EmailAlreadyInUseException.class)
    public ResponseEntity<ErrorResponse> handleEmailInUse(EmailAlreadyInUseException exception) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(exception.getMessage(), HttpStatus.BAD_REQUEST.value()));
    }

    @ExceptionHandler(InvalidPasswordOrEmailException.class)
    public ResponseEntity<ErrorResponse> handleInvalidPasswordOrEmail(InvalidPasswordOrEmailException exception) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ErrorResponse(exception.getMessage(), HttpStatus.UNAUTHORIZED.value()));
    }

    @ExceptionHandler(InvalidTokenException.class)
    public ResponseEntity<ErrorResponse> handleInvalidToken(InvalidTokenException exception) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(exception.getMessage(), HttpStatus.BAD_REQUEST.value()));
    }

    @ExceptionHandler(AccountVerificationException.class)
    public ResponseEntity<ErrorResponse> handleAccountVerification(AccountVerificationException exception) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(new ErrorResponse(exception.getMessage(), HttpStatus.FORBIDDEN.value()));
    }

    @ExceptionHandler(TeacherProfileException.class)
    public ResponseEntity<ErrorResponse> handleTeacherProfile(TeacherProfileException exception) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(new ErrorResponse(exception.getMessage(), HttpStatus.BAD_REQUEST.value()));
    }
}
