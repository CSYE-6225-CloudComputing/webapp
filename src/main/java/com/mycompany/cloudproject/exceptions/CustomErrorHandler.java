package com.mycompany.cloudproject.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class CustomErrorHandler {


    @ExceptionHandler(UserCustomExceptions.class)
    public ResponseEntity<Void> handleUserAlreadyExists(UserCustomExceptions ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .build();
    }


    @ExceptionHandler(UnAuthorizedException.class)
    public ResponseEntity<Void> handleUnAuthorizedException(UnAuthorizedException ex) {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .build();
    }
}
