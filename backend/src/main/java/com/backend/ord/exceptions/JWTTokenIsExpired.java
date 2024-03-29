package com.backend.ord.exceptions;

public class JWTTokenIsExpired extends Exception {
    public JWTTokenIsExpired(String message) {
        super(message);
    }
}
