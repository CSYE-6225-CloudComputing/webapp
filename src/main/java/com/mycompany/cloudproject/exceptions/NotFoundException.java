package com.mycompany.cloudproject.exceptions;

public class NotFoundException  extends Exception {

    public NotFoundException(String errorMessage) {
        super(errorMessage);
    }
}