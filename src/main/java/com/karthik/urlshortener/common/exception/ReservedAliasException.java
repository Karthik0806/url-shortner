package com.karthik.urlshortener.common.exception;

public class ReservedAliasException extends RuntimeException{
    public ReservedAliasException (String message){
        super(message);
    }
}
