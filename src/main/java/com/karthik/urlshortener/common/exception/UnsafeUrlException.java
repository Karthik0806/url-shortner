package com.karthik.urlshortener.common.exception;

public class UnsafeUrlException extends RuntimeException{
    public UnsafeUrlException(String message){
        super(message);
    }
}
