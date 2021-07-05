package com.Banking.Banking.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class GlobalExceptionHandler {


    @ExceptionHandler(value = UserException.class)
    @ResponseStatus(value=HttpStatus.UNAUTHORIZED)
    public ResponseEntity blogNotFoundException(UserException userException) {
        return new ResponseEntity(userException.getMessage(), HttpStatus.UNAUTHORIZED);

    }
}
