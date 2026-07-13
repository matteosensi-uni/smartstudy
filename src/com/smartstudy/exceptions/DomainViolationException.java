package com.smartstudy.exceptions;

public class DomainViolationException extends RuntimeException {
    public DomainViolationException(String message) {
        super(message);
    }
}
