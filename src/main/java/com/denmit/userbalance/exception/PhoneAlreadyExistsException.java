package com.denmit.userbalance.exception;

public class PhoneAlreadyExistsException extends RuntimeException {

    public PhoneAlreadyExistsException() {
        super("Phone already in use");
    }
}
