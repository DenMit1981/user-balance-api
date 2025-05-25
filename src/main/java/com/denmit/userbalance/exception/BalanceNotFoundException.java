package com.denmit.userbalance.exception;

public class BalanceNotFoundException extends RuntimeException {

    public BalanceNotFoundException(String msg) {
        super(msg);
    }
}
