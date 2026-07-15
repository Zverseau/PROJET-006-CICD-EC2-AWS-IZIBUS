package com.example.izibus.exceptions;

public class CompagnieNotFoundException extends RuntimeException {
    public CompagnieNotFoundException(String message) {
        super(message);
    }
}