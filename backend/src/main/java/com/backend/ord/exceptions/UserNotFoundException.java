package com.backend.ord.exceptions;

import lombok.NoArgsConstructor;

import java.util.UUID;

@NoArgsConstructor
public class UserNotFoundException extends Exception{
    public UserNotFoundException(UUID userId) {
        super("User with id " + userId + " not found");
    }
}
