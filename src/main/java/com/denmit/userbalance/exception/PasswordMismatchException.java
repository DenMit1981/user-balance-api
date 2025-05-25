package com.denmit.userbalance.exception;

public class PasswordMismatchException extends RuntimeException {

    public PasswordMismatchException(String msg) {
        super(msg);
    }
}
