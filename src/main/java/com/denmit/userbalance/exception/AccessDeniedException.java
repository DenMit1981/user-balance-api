package com.denmit.userbalance.exception;

public class AccessDeniedException extends RuntimeException {

    public AccessDeniedException(String msg) {
        super(msg);
    }
}

