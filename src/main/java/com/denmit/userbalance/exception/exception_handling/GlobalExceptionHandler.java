package com.denmit.userbalance.exception.exception_handling;

import com.denmit.userbalance.exception.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.*;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Object> resolveHandle(MethodArgumentNotValidException e) {
        Map<String, String> errors = new LinkedHashMap<>();

        e.getBindingResult().getAllErrors()
                .forEach(error -> {
                    String fieldName = ((FieldError) error).getField();
                    String errorMessage = error.getDefaultMessage();
                    errors.put(fieldName, errorMessage);
                });

        List<Map.Entry<String, String>> listErrors = new ArrayList<>(errors.entrySet());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(listErrors);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ExceptionInfo handleException(UserNotFoundException e) {
        return getExceptionInfo(e);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ExceptionInfo handleException(AccountNotFoundException e) {
        return getExceptionInfo(e);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ExceptionInfo handleException(BalanceNotFoundException e) {
        return getExceptionInfo(e);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ExceptionInfo handleException(UserIsPresentException e) {
        return getExceptionInfo(e);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ExceptionInfo handleException(PasswordMismatchException e) {
        return getExceptionInfo(e);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ExceptionInfo handleException(EmailAlreadyExistsException e) {
        return getExceptionInfo(e);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ExceptionInfo handleException(EmailException e) {
        return getExceptionInfo(e);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ExceptionInfo handleException(PhoneException e) {
        return getExceptionInfo(e);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ExceptionInfo handleException(PhoneAlreadyExistsException e) {
        return getExceptionInfo(e);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ExceptionInfo handleException(BalanceException e) {
        return getExceptionInfo(e);
    }

    private ExceptionInfo getExceptionInfo(Exception e) {
        ExceptionInfo info = new ExceptionInfo();
        info.setInfo(e.getMessage());
        return info;
    }
}
