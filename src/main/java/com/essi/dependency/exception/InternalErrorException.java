package com.essi.dependency.exception;

public class InternalErrorException extends ComponentException {

    public InternalErrorException(String message) {
        super(message,400,"Bad request");
    }
}
