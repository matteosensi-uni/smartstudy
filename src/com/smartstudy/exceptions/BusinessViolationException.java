package com.smartstudy.exceptions;

public class BusinessViolationException extends RuntimeException {
    public BusinessViolationException(String message) {
        super(message);
    }
    public BusinessViolationException(BusinessViolationException e){
        super(e.getMessage());
    }
}
