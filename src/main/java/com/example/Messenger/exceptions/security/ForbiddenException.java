package com.example.Messenger.exceptions.security;

public class ForbiddenException extends RuntimeException{
    public ForbiddenException(String msg){
        super(msg);
    }

    public ForbiddenException(){}
}
