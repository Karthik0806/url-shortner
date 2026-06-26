package com.karthik.urlshortener.common.exception;

public class InvalidExpirationException extends RuntimeException {
    public InvalidExpirationException(String message) {
        super(message);
    }
}