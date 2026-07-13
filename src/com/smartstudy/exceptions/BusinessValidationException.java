package com.smartstudy.exceptions;

public class BusinessValidationException extends RuntimeException {
    public BusinessValidationException(String message) {
        super(message);
    }
    public BusinessValidationException(BusinessValidationException e){
        super(e.getMessage());
    }
}
