package com.backend.ord.exceptions;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class NoCorrespondingUserSessionException extends Exception {
    public NoCorrespondingUserSessionException(String message) {
        super(message);
    }
}
