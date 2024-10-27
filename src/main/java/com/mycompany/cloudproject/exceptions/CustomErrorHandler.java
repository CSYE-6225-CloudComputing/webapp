package com.mycompany.cloudproject.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import javax.naming.ServiceUnavailableException;

import java.sql.SQLException;

@ControllerAdvice
public class CustomErrorHandler {


    @ExceptionHandler(UserCustomExceptions.class)
    public ResponseEntity<Void> handleUserAlreadyExists(UserCustomExceptions ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .build();
    }

    @ExceptionHandler({org.springframework.transaction.CannotCreateTransactionException.class,
            java.net.ConnectException.class, SQLException.class, ServiceUnavailableException.class})
    public ResponseEntity<Void> handleDatabaseConnectionException(Exception ex) {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<Void> handleMediaType(Exception ex) {
        return  ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE).build();
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<Void> handleParameters(Exception ex) {
        return  ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Void> handleIncorrectImageType(Exception ex) {
        return  ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }


    @ExceptionHandler(UnAuthorizedException.class)
    public ResponseEntity<Void> handleUnAuthorizedException(UnAuthorizedException ex) {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .build();
    }


}
