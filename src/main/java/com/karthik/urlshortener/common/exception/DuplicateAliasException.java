package com.karthik.urlshortener.common.exception;

public class DuplicateAliasException extends RuntimeException{
    public DuplicateAliasException(String message){
        super(message);
    }
}
