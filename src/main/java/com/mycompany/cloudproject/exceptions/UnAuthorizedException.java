package com.mycompany.cloudproject.exceptions;

public class UnAuthorizedException  extends Exception{

    public UnAuthorizedException(String errorMessage) {
        super(errorMessage);
    }
}
