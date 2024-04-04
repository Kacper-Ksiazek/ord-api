package com.backend.ord.exceptions.REST;

public class PayloadTooLargeException extends RuntimeException{
    public PayloadTooLargeException(String message) {
        super(message);
    }
}
